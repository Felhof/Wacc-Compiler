package compiler.visitors;

import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.NodeElem;
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

  private static final int WORD_SIZE = 4;
  private List<Instr> instructions;
  private List<Instr> data;
  private Set<String> specialLabels;

  private SymbolTable currentST;
  private List<REG> availableRegs;
  private int totalStackOffset;
  private int nextPosInStack;
  private int offsetFromInitialSP;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
    this.data = new ArrayList<>();
    this.specialLabels = new LinkedHashSet<>();
    availableRegs = new ArrayList<>(allUsableRegs);
    offsetFromInitialSP = 0;
  }

  public List<Instr> generate(AST root) {
    totalStackOffset = Integer.parseInt(root.stackOffset());
    nextPosInStack = totalStackOffset;
    constructStartProgram();
    visitFuncsAndChildren(root);
    constructEndProgram();
    for (String s : specialLabels) {
      addSpecialFunction(s);
    }
    data.addAll(instructions);
    return data;
  }

  private void visitFuncsAndChildren(AST root) {
    root.root().children().stream().filter(node -> node instanceof FuncNode)
        .forEach(
            this::visit);
    currentST = root.symbolTable();
    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));
    configureStack("sub");
    root.root().children().stream().filter(node -> !(node instanceof FuncNode))
        .forEach(
            this::visit);

  }

  public void visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
  }

  private void configureStack(String type) {
    int maxIntImmShift = 1024;
    if (totalStackOffset > 0) {
      int temp = totalStackOffset;
      while (temp / 1024 != 0) {
        instructions.add(buildInstr(type, SP, SP, new Imm_INT(maxIntImmShift)));
        temp = temp - 1024;
      }
      instructions.add(buildInstr(type, SP, SP,
          new Imm_INT(totalStackOffset % maxIntImmShift)));
    }
  }

  private Instr buildInstr(String type, REG rd, REG rn, Operand op2) {
    switch (type) {
      case "addExpr":
        return new ADD(rd, rn, op2);
      case "sub":
        return new SUB(rd, rn, op2);
      default:
        return null;
    }
  }

  private void constructEndProgram() {
    configureStack("addExpr");
    setArg(new Imm_INT_MEM(toInt("0")));
    instructions.add(new POP(PC));
    instructions.add(new SECTION("ltorg"));
  }

  private void constructStartProgram() {
    data.add(new SECTION("data"));
    instructions.add(new SECTION("text"));
    instructions.add(new SECTION("main", true));
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
    REG rd = topRegAvailable();
    instructions.add(new LDR(rd, new Imm_INT_MEM(toInt(expr.value())), false));
    return rd;
  }

  public CodeGenData visitBinaryExp(BinExpr binExpr) {
    REG rd1 = (REG) visit(binExpr.rhs());
    availableRegs.remove(0);
    REG rd2 = (REG) visit(binExpr.lhs());
    if(binExpr.operator().equals(BinExpr.BINOP.AND)) {
      instructions.add(new AND(rd1, rd1, rd2));
    } else if (binExpr.operator().equals(BinExpr.BINOP.OR)) {
      instructions.add(new ORR(rd1, rd1, rd2));
    }
    availableRegs.add(0 , rd1);
    return rd1;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode) {
    REG rd = (REG) visit(printNode.expr());
    // mov result into arg register
    instructions.add(new MOV(R0, rd));

    if (printNode.expr().type().equals(CharType.getInstance())) {
      jumpToFunctionLabel("putchar");
    } else if (printNode.expr().type().equals(IntType.getInstance())) {
      jumpToFunctionLabel("p_print_int");
      specialLabels.add("p_print_int");
    } else if (printNode.expr().type().equals(BoolType.getInstance())) {
      jumpToFunctionLabel("p_print_bool");
      specialLabels.add("p_print_bool");
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

  public CodeGenData visitReadExpr(ReadNode readNode) {
    //REG rd = (REG) visit((ASTData) readNode.lhs());

    //In this case we don't visit the Node because we don't want to store the value but the address
    REG rd = useFreeReg();
    instructions.add(new ADD(rd, SP, new Imm_INT(
        currentST.lookUpAllVar(readNode.lhs().varName()).getStackOffset() - offsetFromInitialSP)));

    instructions.add(new MOV(R0, rd));

    if (((NodeElem) readNode.lhs()).type().equals(IntType.getInstance())) {
      instructions.add(new BL("p_read_int"));
      specialLabels.add("p_read_int");
    } else if (((NodeElem) readNode.lhs()).type()
        .equals(CharType.getInstance())) {
      instructions.add(new BL("p_read_char"));
      specialLabels.add("p_read_char");
    }

    return null;
  }

  public CodeGenData visitStringExpr(StringExpr stringExpr) {
    String labelName = addStringField(stringExpr.getValue());
    REG rd = topRegAvailable();
    instructions.add(new LDR(rd, new Imm_STRING_MEM(labelName), false));
    return rd;
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (data.size() / 2);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }

  public CodeGenData visitCharExpr(CharExpr charExpr) {
    REG rd = topRegAvailable();
    instructions
        .add(new MOV(rd, new Imm_STRING("'" + charExpr.getValue() + "'")));
    return rd;
  }

  public CodeGenData visitBoolExpr(BoolExpr boolExpr) {
    REG rd = topRegAvailable();
    instructions.add(new MOV(rd, new Imm_INT(boolExpr.value() ? 1 : 0)));
    return rd;
  }

  // Refactor possibly using overloading in accept of VarDeclareNode
  // For now its ok.
  public CodeGenData visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    Type type = varDeclareNode.varType();
    if (type instanceof ArrType) {
      visitArrayDeclare(varDeclareNode);
    } else if (type instanceof PairType) {
      visitPairDeclare(varDeclareNode);
    } else {
      visitBasicTypeDeclare(varDeclareNode, (type instanceof IntType ? 4 : 1));
    }
    return null;
  }

  private CodeGenData visitBasicTypeDeclare(VarDeclareNode varDeclareNode,
      int offset) {
    REG rd = (REG) visit(varDeclareNode.rhs());
    nextPosInStack -= offset;
    currentST.lookUpAllVar(varDeclareNode.varName())
        .setStackOffset(nextPosInStack);
    return saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack,
        false);
  }

  public CodeGenData visitAssignNode(VarAssignNode varAssignNode) {
    //TODO: IMPLEMENT OTHER TYPES OF RHS
    if (varAssignNode.rhs() instanceof Ident) {
      visitIdentAssign(varAssignNode);
    }
    return null;
  }

  private void visitIdentAssign(VarAssignNode varAssignNode) {
    //TODO: CONSIDER other cases such as Arrays or Pairs
    REG rd = (REG) visit(varAssignNode.rhs());
    int offset = currentST.lookUpAllVar(varAssignNode.lhs().varName())
        .getStackOffset() - offsetFromInitialSP;
    saveVarData(varAssignNode.rhs().type(), rd, SP, offset, false);
  }

  private CodeGenData saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    boolean isByteInstr =
        varType.equals(BoolType.getInstance()) || varType
            .equals(CharType.getInstance());
    instructions
        .add(new STR(rd, new Addr(rn, true, new Imm_INT(offset)), isByteInstr,
            update));
    return null;
  }

  private CodeGenData visitArrayDeclare(VarDeclareNode varDeclareNode) {
    return null;
  }

  private CodeGenData visitPairDeclare(VarDeclareNode varDeclareNode) {
    setArg(new Imm_INT_MEM(2 * WORD_SIZE));
    instructions.add(new BL("malloc"));
    REG rd = useFreeReg();
    instructions.add(new MOV(rd, R0)); // fetch address of pair
    Pair pair = (Pair) varDeclareNode.rhs();
    storeExpInHeap(pair.fst(), rd, 0);
    storeExpInHeap(pair.snd(), rd, 4);
    instructions.add(new STR(rd, new Addr(SP), false, false));
    availableRegs.add(rd);
    return null;
  }

  private void storeExpInHeap(Expr expr, REG objectAddr, int offset) {
    REG rd = (REG) visit(expr);
    setArg(new Imm_INT_MEM(expr.sizeOf()));
    instructions.add(new BL("malloc"));
    saveVarData(expr.type(), rd, R0, 0, false);
    saveVarData(expr.type(), R0, objectAddr, offset, false);
  }

  private void setArg(Operand op2) {
    instructions.add(new LDR(R0, op2, false));
  }

  private void addSpecialFunction(String name) {
    switch (name) {
      case "p_print_string":
        addPrintString();
        break;
      case "p_print_int":
        addPrintInt();
        break;
      case "p_print_bool":
        addPrintBool();
        break;
      case "p_print_ln":
        addPrintln();
        break;
      case "p_read_int":
        addReadInt();
        break;
      case "p_read_char":
        addReadChar();
        break;
    }
  }

  private void addPrintString() {
    String labelName = addStringField("\"%.*s\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_string"),
        new PUSH(LR),
        new LDR(R1, new Addr(R0), false),
        new ADD(R2, R0, new Imm_INT(toInt("4"))),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(toInt("4")))));
    jumpToFunctionLabel("printf");
    instructions.addAll(Arrays.asList(
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));

  }

  private void addPrintInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_int"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4))));

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
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(toInt("4"))),
        new BL("puts"),
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));
  }

  private void addPrintBool() {
    String trueLabel = addStringField("\"true\\0\"");
    String falseLabel = addStringField("\"false\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_bool"),
        new PUSH(LR),
        new CMP(R0, new Imm_INT(0)),
        new LDR_COND(R0, new Imm_STRING_MEM(trueLabel), LDR_COND.COND.NE),
        new LDR_COND(R0, new Imm_STRING_MEM(falseLabel), LDR_COND.COND.EQ),
        new ADD(R0, R0, new Imm_INT(4))));

    jumpToFunctionLabel("printf");

    instructions.addAll(Arrays.asList(
        new MOV(R0, new Imm_INT(0)),
        new BL("fflush"),
        new POP(PC)
    ));
  }

  private void addReadInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_read_int"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4)),
        new BL("scanf"),
        new POP(PC)
    ));
  }

  private void addReadChar() {
    String labelName = addStringField("\"%c\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_read_char"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4)),
        new BL("scanf"),
        new POP(PC)
    ));
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

  private REG topRegAvailable() {
    return availableRegs.get(0);
  }

  private REG useFreeReg() {
    return availableRegs.remove(0);
  }

  private int toInt(String s) {
    return Integer.parseInt(s);
  }

  public CodeGenData visitIdent(Ident ident) {
    REG rd = topRegAvailable();
    int offset = currentST.lookUpAllVar(ident.varName()).getStackOffset() - offsetFromInitialSP;
    instructions.add(new LDR(rd, new Addr(SP, true,
        new Imm_INT(
          offset)), ident.sizeOf() == 1));
    return rd;
  }

  public CodeGenData visitFuncNode(FuncNode funcNode) {
    currentST = funcNode.symbolTable();
    instructions.add(new LABEL("f_" + funcNode.name()));
    instructions.add(new PUSH(LR));
    if (!funcNode.paramList().exprList().isEmpty())
      visit(funcNode.paramList());
    funcNode.getParentNode().children().forEach(this::visit);
    instructions.addAll(Arrays.asList(new POP(PC), new POP(PC)));
    currentST = currentST.getEncSymTable();
    instructions.add(new SECTION("ltorg"));
    return null;
  }

  public CodeGenData visitReturn(ReturnNode returnNode) {
    REG rd = (REG) visit(returnNode.expr());
    instructions.add(new MOV(R0, rd));
    return null;
  }

  public CodeGenData visitFuncCall(FuncCall funcCall) {
    visit(funcCall.argsList());
    jumpToFunctionLabel("f_" + funcCall.funcName());
    offsetFromInitialSP = 0;
    instructions.add(new ADD(SP, SP, new Imm_INT(funcCall.argsList().bytesPushed())));
    REG rd = topRegAvailable();
    instructions.add(new MOV(rd, R0));
    return rd;
  }

  public CodeGenData visitParams(ListExpr listExpr) {
    List<String> paramNames = listExpr.paramNames();
    List<Expr> exprList = listExpr.exprList();
    int offset =
        listExpr.bytesPushed() + exprList.get(exprList.size() - 1).sizeOf();
    for (int i = exprList.size() - 1; i >= 0; i--) {
      offset -= exprList.get(i).sizeOf();
      currentST.lookUpAllVar(paramNames.get(i)).setStackOffset(offset);
    }
    return null;
  }

  public CodeGenData visitArgs(ListExpr listExpr) {
    List<Expr> reverseArgs = new ArrayList<>(listExpr.exprList());
    Collections.reverse(reverseArgs);
    for (Expr e : reverseArgs) {
      REG rd = (REG) visit(e);
      int offsetFromBase = (e.type() instanceof IntType) ? -4 : -1;
      offsetFromInitialSP += offsetFromBase;
      saveVarData(e.type(), rd, SP, offsetFromBase,
          true);
    }
    return null;
  }
}
