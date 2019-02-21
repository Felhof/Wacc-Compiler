package compiler.visitors;

import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.RHS.*;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.*;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.ArrType;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.IntType;
import compiler.AST.Types.PairType;
import compiler.AST.Types.Type;
import compiler.instr.*;
import compiler.instr.Operand.*;
import compiler.instr.BL;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.MOV;
import compiler.instr.POP;
import compiler.instr.PUSH;
import compiler.instr.REG;

import java.util.*;
import java.util.Collections;

import static compiler.instr.REG.*;

public class ASTVisitor {

  private List<Instr> instructions;
  private List<Instr> data;
  private Set<String> specialLabels;

  private SymbolTable currentST;
  private List<REG> availableRegs;
  private int currentStackOffset;
  private Map<String, Integer> varToOffsetFromStack;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
    this.data = new ArrayList<>();
    this.specialLabels = new HashSet<>();
    availableRegs = new ArrayList<>(allUsableRegs);
    varToOffsetFromStack = new HashMap<>();
  }

  public List<Instr> generate(AST root) {
    int totalStackOffset = Integer.parseInt(root.stackOffset());
    currentStackOffset = totalStackOffset;
    constructStartProgram();
    if (Integer.parseInt(root.stackOffset()) > 0) {
      instructions.add(new SUB(SP, SP, new Imm_INT(totalStackOffset)));
    }
    currentST = root.symbolTable();
    visitParentNode(root.root());
    if (Integer.parseInt(root.stackOffset()) > 0) {
      instructions.add(new ADD(SP, SP, new Imm_INT(totalStackOffset)));
    }
    constructEndProgram();

    for (String s : specialLabels) {
      addSpecialFunction(s);
    }
    data.addAll(instructions);
    return data;
  }

  private void constructEndProgram() {
    instructions.add(new LDR(R0, new Imm_INT_LDR("0")));
    instructions.add(new POP(PC));
    instructions.add(new SECTION("ltorg"));
  }

  private void constructStartProgram() {
    data.add(new SECTION("data"));
    instructions.add(new SECTION("text"));
    instructions.add(new SECTION("main", true));
    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));
  }

  public void visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
  }

  private CodeGenData visit(ASTData data) {
    return data.accept(this);
  }

  public void visitExit(ExitNode exitNode) {
    REG rd = (REG) visit(exitNode.exitStatus());
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("exit"));
  }

  public CodeGenData visitUnaryExpr(UnaryExpr expr) {

    if (expr.type().equals(IntType.getInstance()) // Set int value to negative
      && expr.operator() == UNOP.MINUS) {
      ((IntExpr) expr.insideExpr()).setNegative();
    }

    //TODO: handle other types

    return visit(expr.insideExpr());
  }

  public CodeGenData visitIntExpr(IntExpr expr) {
    REG rd = useFreeReg();
    instructions.add(new LDR(rd, new Imm_INT_LDR(expr.value())));
    return rd;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode) {
    REG rd = (REG) visit(printNode.expr());
    // mov result into arg register
    instructions.add(new MOV(R0, rd));

    if (printNode.expr().type().equals(CharType.getInstance())) {
      jumpToFunctionLabel("putchar");
    } else {
      jumpToFunctionLabel("p_print_string");
      specialLabels.add("p_print_string");
    }

    if (printNode.newLine()) {
      jumpToFunctionLabel("p_print_ln");
      specialLabels.add("p_print_ln");
    }
    return null;
  }

  public CodeGenData visitStringExpr(StringExpr stringExpr) {
    String labelName = addStringField(stringExpr.getValue());
    REG rd = useFreeReg();
    instructions.add(new LDR(rd, new Imm_STRING_LDR(labelName)));
    return rd;
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (data.size() - 1);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }

  public CodeGenData visitCharExpr(CharExpr charExpr) {
    REG rd = useFreeReg();
    instructions
      .add(new MOV(rd, new Imm_STRING("'" + charExpr.getValue() + "'")));
    return rd;
  }

  public CodeGenData visitBoolExpr(BoolExpr boolExpr) {
    REG rd = useFreeReg();
    instructions.add(new MOV(rd, new Imm_INT(boolExpr.value() ? 1 : 0)));
    return rd;
  }

  // Refactor possibly using overloading in accept of VarDeclareNode
  // For now its ok.
  public CodeGenData visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    Type type = varDeclareNode.varType();

    if (type.equals(IntType.getInstance())) {
      visitBasicTypeDeclare(varDeclareNode, 4);
    }

    if (type.equals(CharType.getInstance()) || type
      .equals(BoolType.getInstance())) {
      visitBasicTypeDeclare(varDeclareNode, 1);
    }

    if (type instanceof ArrType) {
      visitArrayDeclare(varDeclareNode);
    }

    if (type instanceof PairType) {
      visitPairDeclare(varDeclareNode);
    }

    return null;
  }

  private CodeGenData visitBasicTypeDeclare(VarDeclareNode varDeclareNode,
    int offset) {
    REG rd = (REG) visit(varDeclareNode.rhs());
    currentStackOffset -= offset;
    varToOffsetFromStack.put(varDeclareNode.varName(), currentStackOffset);
    boolean isByteInstr =
      varDeclareNode.varType().equals(BoolType.getInstance()) || varDeclareNode.varType()
        .equals(CharType.getInstance());
    instructions
      .add(new STR(rd, new Addr(SP, true, new Imm_INT(currentStackOffset)), isByteInstr));
    return null;
  }

  private CodeGenData visitArrayDeclare(VarDeclareNode varDeclareNode) {
    return null;
  }

  private CodeGenData visitPairDeclare(VarDeclareNode varDeclareNode) {
    return null;
  }


  private void addSpecialFunction(String name) {
    switch (name) {
      case "p_print_string":
        addPrint();
        break;

      case "p_print_ln":
        addPrintln();
        break;
    }
  }

  private void addPrint() {
    String labelName = addStringField("\"%.*s\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_string"),
      new PUSH(LR),
      new LDR(R1, new Addr(R0)),
      new ADD(R2, R0, new Imm_INT(toInt("4"))),
      new LDR(R0, new Imm_STRING_LDR(labelName)),
      new ADD(R0, R0, new Imm_INT(toInt("4")))));
    jumpToFunctionLabel("printf");
    instructions.addAll(Arrays.asList(
      new MOV(R0, new Imm_INT(toInt("0"))),
      new BL("fflush"),
      new POP(PC)));

  }

  private void addPrintln() {
    String labelName = addStringField("\"\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_ln"),
      new PUSH(LR),
      new LDR(R0, new Imm_STRING_LDR(labelName)),
      new ADD(R0, R0, new Imm_INT(toInt("4"))),
      new BL("puts"),
      new MOV(R0, new Imm_INT(toInt("0"))),
      new BL("fflush"),
      new POP(PC)));
  }

  private void jumpToFunctionLabel(String label) {
    List<REG> usedRegs = getUsedRegs();
    if (!usedRegs.isEmpty()) {
      instructions.add(new PUSH(usedRegs)); // save onto stack all used regs
    }
    instructions.add(new BL(label));
    if (!usedRegs.isEmpty()) {
      Collections.reverse(usedRegs);
      instructions.add(new POP(usedRegs));  // restore previous regs from stack
    }
  }

  private List<REG> getUsedRegs() {
    List<REG> regs = new ArrayList<>(allUsableRegs);
    regs.removeAll(availableRegs);
    return regs;
  }

  private REG useFreeReg() {
    return R4;
  }

  private int toInt(String s) {
    return Integer.parseInt(s);
  }

  public CodeGenData visitIdent(Ident ident) {
    REG rd = useFreeReg();
    instructions.add(new LDR(rd, new Addr(SP, true,
      new Imm_INT(varToOffsetFromStack.get(ident.varName())))));
    return rd;
  }
}
