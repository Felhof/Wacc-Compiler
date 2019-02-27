package compiler.visitors.backend;

import static compiler.IR.Operand.REG.LR;
import static compiler.IR.Operand.REG.PC;
import static compiler.IR.Operand.REG.R0;
import static compiler.IR.Operand.REG.SP;
import static compiler.IR.Operand.REG.allUsableRegs;

import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.FreeNode;
import compiler.AST.Nodes.FuncNode;
import compiler.AST.Nodes.IfElseNode;
import compiler.AST.Nodes.Node;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.Nodes.PrintNode;
import compiler.AST.Nodes.ReadNode;
import compiler.AST.Nodes.ReturnNode;
import compiler.AST.Nodes.ScopeNode;
import compiler.AST.Nodes.VarAssignNode;
import compiler.AST.Nodes.VarDeclareNode;
import compiler.AST.Nodes.WhileNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.ArrType;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.IntType;
import compiler.IR.Instructions.ADD;
import compiler.IR.Instructions.B;
import compiler.IR.Instructions.CMP;
import compiler.IR.Instructions.Instr;
import compiler.IR.Instructions.LABEL;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Instructions.MOV;
import compiler.IR.Instructions.POP;
import compiler.IR.Instructions.PUSH;
import compiler.IR.Instructions.SECTION;
import compiler.IR.Instructions.SUB;
import compiler.IR.Operand.Imm_INT;
import compiler.IR.Operand.Imm_INT_MEM;
import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;
import compiler.IR.SubRoutines;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor extends CodegenVisitor {
  /* Code generator visitor to visit AST Node */

  public static final int MAX_INT_IMM_SHIFT = 1024;

  private int nextPosInStack;
  private int totalStackOffset;
  private int branchNb = 0;

  private NodeElemVisitor nodeElemVisitor;

  public ASTVisitor() {
    instructions = new ArrayList<>();
    subroutines = new SubRoutines();
    availableRegs = new ArrayList<>(allUsableRegs);
    nodeElemVisitor = new NodeElemVisitor();
  }

  /* Code generator setup */

  public List<Instr> generate(AST root) {
    constructStartProgram();
    visitFuncsAndChildren(root);
    constructEndProgram();
    // add all data fields at the beginning
    instructions.addAll(0, subroutines.getDataFields());
    // add at all subroutines at the end
    instructions.addAll(subroutines.getInstructions());
    return instructions;
  }

  private void constructStartProgram() {
    instructions.add(new SECTION("text"));
    instructions.add(new SECTION("main", true));
  }

  private void constructEndProgram() {
    configureStack("add");
    loadArg(new Imm_INT_MEM(0), false);
    instructions.add(new POP(PC));
    instructions.add(new SECTION("ltorg"));
  }

  private void visitFuncsAndChildren(AST root) {
    root.root().children().stream().filter(node -> node instanceof FuncNode)
        .forEach(f -> {
          scopeStackOffset = ((FuncNode) f).stackOffset();
          totalStackOffset = scopeStackOffset;
          nextPosInStack = scopeStackOffset;
          visit(f);
        });
    scopeStackOffset = Integer.parseInt(root.stackOffset());
    totalStackOffset = scopeStackOffset;
    nextPosInStack = scopeStackOffset;
    enterScope(root.symbolTable());
    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));
    configureStack("sub");
    root.root().children().stream().filter(node -> !(node instanceof FuncNode))
        .forEach(
            this::visit);

  }

  private void configureStack(String type) {
    if (scopeStackOffset > 0) {
      int temp = scopeStackOffset;
      while (temp / MAX_INT_IMM_SHIFT != 0) {
        instructions.add(buildInstr(type, new Imm_INT(MAX_INT_IMM_SHIFT)));
        temp = temp - MAX_INT_IMM_SHIFT;
      }
      instructions.add(buildInstr(type,
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

  /* Ast nodes visit methods */

  private void visit(Node node) {
    node.accept(this);
  }

  public void visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
  }

  public void visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    REG rd = nodeElemVisitor.visit(varDeclareNode.rhs());
    nextPosInStack -= varDeclareNode.varType().getSize();

    // store variable in the stack and save offset in symbol table
    currentST.lookUpAllVar(varDeclareNode.varName())
        .setLocalOffset(nextPosInStack);
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack, false);

    freeReg(rd);
  }

  public void visitAssignNode(VarAssignNode varAssignNode) {
    REG rd = nodeElemVisitor.visit(varAssignNode.rhs());
    REG rn = nodeElemVisitor.visit(varAssignNode.lhs());

    saveVarData(varAssignNode.lhs().type(), rd, rn, 0, false);

    freeReg(rd);
    freeReg(rn);
  }

  public void visitExitNode(ExitNode exitNode) {
    REG rd = nodeElemVisitor.visit(exitNode.exitStatus());
    instructions.add(new MOV(R0, rd));
    instructions.add(new B("exit", true));
    freeReg(rd);
  }

  public void visitPrintNode(PrintNode printNode) {
    REG rd = nodeElemVisitor.visit(printNode.expr());

    // mov result into arg register
    moveArg(rd);
    String printLabel;

    if (printNode.expr().type().equals(CharType.getInstance())) {
      instructions.add(new B("putchar", true));
    } else if (printNode.expr().type().equals(IntType.getInstance())) {
      printLabel = subroutines.addPrintInt();
      instructions.add(new B(printLabel, true));
    } else if (printNode.expr().type().equals(BoolType.getInstance())) {
      printLabel = subroutines.addPrintBool();
      instructions.add(new B(printLabel, true));
    } else if (printNode.expr().type().equals(ArrType.stringType())) {
      printLabel = subroutines.addPrintString();
      instructions.add(new B(printLabel, true));
    } else {
      printLabel = subroutines.addPrintReference();
      instructions.add(new B(printLabel, true));
    }

    if (printNode.newLine()) {
      printLabel = subroutines.addPrintln();
      instructions.add(new B(printLabel, true));
    }

    freeReg(rd);
  }

  public void visitReadNode(ReadNode readNode) {
    REG rd = nodeElemVisitor.visit(readNode.lhs());
    String readLabel;

    moveArg(rd);
    if ((readNode.lhs()).type().equals(IntType.getInstance())) {
      readLabel = subroutines.addReadInt();
      instructions.add(new B(readLabel, true));
    } else if ((readNode.lhs()).type().equals(CharType.getInstance())) {
      readLabel = subroutines.addReadChar();
      instructions.add(new B(readLabel, true));
    }

    freeReg(rd);
  }

  public void visitFuncNode(FuncNode funcNode) {
    enterScope(funcNode.symbolTable());
    instructions.add(new LABEL("f_" + funcNode.name()));
    instructions.add(new PUSH(LR));
    configureStack("sub");
    if (!funcNode.paramList().exprList().isEmpty()) {
      nodeElemVisitor.visit(funcNode.paramList());
    }
    visit(funcNode.getParentNode());
    instructions.add(new POP(PC));
    instructions.add(new SECTION("ltorg"));
    exitScope(currentST.getEncSymTable());
  }

  public void visitFreeNode(FreeNode freeNode) {
    REG rd = nodeElemVisitor.visit(freeNode.freeExpr());
    instructions.add(new MOV(R0, rd));
    String freePairLabel = subroutines.addFreePairCheck();
    instructions.add(new B(freePairLabel, true));
    freeReg(rd);
  }

  public void visitIfElseNode(IfElseNode ifElseNode) {
    REG rd = nodeElemVisitor.visit(ifElseNode.cond());
    instructions.add(new CMP(rd, new Imm_INT(0)));
    instructions.add(new B("L" + branchNb, COND.EQ));
    freeReg(rd);
    int scopeBranchNb = branchNb;
    branchNb += 2;

    visitChildStats(ifElseNode.thenST(), ifElseNode.thenStat());
    instructions.add(new B("L" + (scopeBranchNb + 1)));
    instructions.add(new LABEL("L" + (scopeBranchNb)));
    visitChildStats(ifElseNode.elseST(), ifElseNode.elseStat());
    instructions.add(new LABEL("L" + (scopeBranchNb + 1)));
  }

  public void visitWhileNode(WhileNode whileNode) {
    instructions.add(new B("L" + branchNb));

    int condBranchNb = branchNb;
    branchNb += 2;
    // Add label for do statement
    instructions.add(new LABEL("L" + (condBranchNb + 1)));

    visitChildStats(whileNode.statST(), whileNode.parentNode());

    // Add label for condition
    instructions.add(new LABEL("L" + condBranchNb));
    REG rd = nodeElemVisitor.visit(whileNode.condition());
    instructions.add(new CMP(rd, new Imm_INT(1)));
    instructions.add(new B("L" + (condBranchNb + 1), COND.EQ));
    freeReg(rd);
  }

  public void visitReturnNode(ReturnNode returnNode) {
    REG rd = nodeElemVisitor.visit(returnNode.expr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new ADD(SP, SP, new Imm_INT(totalStackOffset)));
    instructions.add(new POP(PC));
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

  private void enterScope(SymbolTable symbolTable) {
    currentST = symbolTable;
  }

  private void exitScope(SymbolTable encSymTable) {
    currentST = encSymTable;
  }

}