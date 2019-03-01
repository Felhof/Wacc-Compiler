package compiler.visitors.backend;

import static compiler.IR.Operand.REG.*;
import static compiler.visitors.backend.ASTVisitor.visit;

import compiler.AST.Nodes.*;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.*;
import compiler.IR.IR;
import compiler.IR.Instructions.*;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Operand.*;
import compiler.IR.Subroutines;
import java.util.Arrays;
import java.util.List;

// Visitor responsible to visit AST Nodes (corresponding to wacc statements)
// and populate the IR of the program
public class NodeVisitor extends CodeGenerator {

  public static final int MAX_INT_IMM_SHIFT = 1024;

  private int nextPosInStack;
  private int totalStackOffset;
  private int branchNb = 0;

  public NodeVisitor(IR program, Subroutines subroutines, List<REG> availableRegs) {
    super(program, subroutines, availableRegs);
  }

  // AST root visit methods that setup code generation fields and visit all
  // instructions

  //method starts by visiting all functions and proceeds to main
  void visitFuncsAndChildren(AST root) {
    root.funcNodes().forEach(ASTVisitor::visit);
    scopeStackOffset = totalStackOffset = nextPosInStack = root.stackOffset();
    enterScope(root.symbolTable());
    addMainStart();
    root.root().children().forEach(ASTVisitor::visit);
    addMainEnd();
  }

  private void addMainStart() {
    program.addInstr(new LABEL("main"));
    program.addInstr(new PUSH(LR));
    configureStack("sub");
  }

  private void addMainEnd() {
    configureStack("add");
    program.addAllInstr(Arrays.asList(
        new LDR(R0, new Imm_INT_MEM(0)),
        new POP(PC),
        new SECTION("ltorg")));
  }

  private void configureStack(String type) {
    if (scopeStackOffset > 0) {
      int temp = scopeStackOffset;
      while (temp / MAX_INT_IMM_SHIFT != 0) {
        program.addInstr(buildInstr(type, new Imm_INT(MAX_INT_IMM_SHIFT)));
        temp = temp - MAX_INT_IMM_SHIFT;
      }
      program.addInstr(buildInstr(type,
          new Imm_INT(scopeStackOffset % MAX_INT_IMM_SHIFT)));
    }
  }

  private Instr buildInstr(String type, Operand op2) {
    switch (type) {
      case "add":
        return new ADD(REG.SP, REG.SP, op2);
      case "sub":
        return new SUB(REG.SP, REG.SP, op2);
      default:
        return null;
    }
  }

  // Ast node visit methods

  public void visitParentNode(ParentNode node) {
    node.children().forEach(ASTVisitor::visit);
  }

  public void visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    REG rd = visit(varDeclareNode.rhs());
    nextPosInStack -= varDeclareNode.varType().getSize();

    // store variable in the stack and save offset in symbol table
    currentST.lookUpAllVar(varDeclareNode.varName())
        .setLocalOffset(nextPosInStack);
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack, false);

    freeReg(rd);
  }

  public void visitAssignNode(VarAssignNode varAssignNode) {
    REG rd = visit(varAssignNode.rhs());
    REG rn = visit(varAssignNode.lhs());

    saveVarData(varAssignNode.lhs().type(), rd, rn, 0, false);

    freeReg(rd);
    freeReg(rn);
  }

  public void visitExitNode(ExitNode exitNode) {
    REG rd = visit(exitNode.exitStatus());
    program.addInstr(new MOV(R0, rd));
    program.addInstr(new B("exit", true));
    freeReg(rd);
  }

  public void visitPrintNode(PrintNode printNode) {
    REG rd = visit(printNode.expr());

    moveArg(rd);
    // use Subroutines class to add print label corresponding to the expr type
    String printLabel = subroutines.addPrint(printNode.expr().type());
    program.addInstr(new B(printLabel, true));

    if (printNode.newLine()) {
      printLabel = subroutines.addPrintln();
      program.addInstr(new B(printLabel, true));
    }

    freeReg(rd);
  }

  public void visitReadNode(ReadNode readNode) {
    REG rd = visit(readNode.lhs());

    moveArg(rd);
    String readLabel = subroutines.addRead(readNode.lhs().type());
    program.addInstr(new B(readLabel, true));

    freeReg(rd);
  }

  public void visitFuncNode(FuncNode funcNode) {
    scopeStackOffset = funcNode.stackOffset();
    totalStackOffset = nextPosInStack = scopeStackOffset;
    enterScope(funcNode.symbolTable());
    program.addInstr(new LABEL("f_" + funcNode.name()));
    program.addInstr(new PUSH(LR));
    configureStack("sub");
    if (!funcNode.paramList().exprList().isEmpty()) {
      visit(funcNode.paramList());
    }
    visit(funcNode.getParentNode());
    program.addInstr(new POP(PC));
    program.addInstr(new SECTION("ltorg"));
    exitScope(currentST.getEncSymTable());
  }

  public void visitFreeNode(FreeNode freeNode) {
    REG rd = visit(freeNode.freeExpr());
    program.addInstr(new MOV(R0, rd));
    String freePairLabel = subroutines.addFreePairCheck();
    program.addInstr(new B(freePairLabel, true));
    freeReg(rd);
  }

  public void visitIfElseNode(IfElseNode ifElseNode) {
    REG rd = visit(ifElseNode.cond());
    program.addInstr(new CMP(rd, new Imm_INT(0)));
    program.addInstr(new B("L" + branchNb, COND.EQ));
    freeReg(rd);
    int scopeBranchNb = branchNb;
    branchNb += 2;

    visitChildStats(ifElseNode.thenST(), ifElseNode.thenStat());
    program.addInstr(new B("L" + (scopeBranchNb + 1)));
    program.addInstr(new LABEL("L" + (scopeBranchNb)));
    visitChildStats(ifElseNode.elseST(), ifElseNode.elseStat());
    program.addInstr(new LABEL("L" + (scopeBranchNb + 1)));
  }

  public void visitWhileNode(WhileNode whileNode) {
    program.addInstr(new B("L" + branchNb));

    int condBranchNb = branchNb;
    branchNb += 2;
    // Add label for do statement
    program.addInstr(new LABEL("L" + (condBranchNb + 1)));

    visitChildStats(whileNode.statST(), whileNode.parentNode());

    // Add label for condition
    program.addInstr(new LABEL("L" + condBranchNb));
    REG rd = visit(whileNode.condition());
    program.addInstr(new CMP(rd, new Imm_INT(1)));
    program.addInstr(new B("L" + (condBranchNb + 1), COND.EQ));
    freeReg(rd);
  }

  public void visitReturnNode(ReturnNode returnNode) {
    REG rd = visit(returnNode.expr());
    program.addInstr(new MOV(R0, rd));
    program.addInstr(new ADD(SP, SP, new Imm_INT(totalStackOffset)));
    program.addInstr(new POP(PC));
    freeReg(rd);
  }

  public void visitScopeNode(ScopeNode scopeNode) {
    visitChildStats(scopeNode.symbolTable(), scopeNode.parentNode());
  }


  /* Util methods specific to AST Visitor */

  private void visitChildStats(SymbolTable st, ParentNode child) {
    int[] tempValues = setDynamicFields(st.getStackOffset());
    configureStack("sub");
    enterScope(st);
    visit(child);
    exitScope(currentST.getEncSymTable());
    configureStack("add");
    reinstateDynamicsFields(tempValues);
  }

  private void reinstateDynamicsFields(int[] tempValues) {
    totalStackOffset = tempValues[0];
    scopeStackOffset = tempValues[1];
    nextPosInStack = tempValues[2];
  }

  private int[] setDynamicFields(int scopeOffset) {
    int[] tempValues = {totalStackOffset, scopeStackOffset, nextPosInStack};
    scopeStackOffset = scopeOffset;
    totalStackOffset = scopeStackOffset + tempValues[0];
    nextPosInStack = scopeStackOffset;
    return tempValues;
  }

  // helper methods to set the current Symbol table to the appropriate scope
  private void enterScope(SymbolTable symbolTable) {
    currentST = symbolTable;
  }

  private void exitScope(SymbolTable encSymTable) {
    currentST = encSymTable;
  }

}