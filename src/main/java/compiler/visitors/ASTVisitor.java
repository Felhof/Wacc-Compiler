package compiler.visitors;

import static compiler.SubRoutines.addSpecialFunction;
import static compiler.instr.REG.LR;
import static compiler.instr.REG.PC;
import static compiler.instr.REG.R0;
import static compiler.instr.REG.R1;
import static compiler.instr.REG.R10;
import static compiler.instr.REG.SP;
import static compiler.instr.REG.allUsableRegs;
import static compiler.instr.Shift.SHIFT_TYPE.ASR;
import static compiler.instr.Shift.SHIFT_TYPE.LSL;

import com.sun.org.apache.regexp.internal.RE;
import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.LHS;
import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.ArrayLiter;
import compiler.AST.NodeElements.RHS.BinExpr;
import compiler.AST.NodeElements.RHS.BoolExpr;
import compiler.AST.NodeElements.RHS.CharExpr;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.NodeElements.RHS.FuncCall;
import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.NodeElements.RHS.Pair;
import compiler.AST.NodeElements.RHS.StringExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.FreeNode;
import compiler.AST.Nodes.FuncNode;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.Nodes.PrintNode;
import compiler.AST.Nodes.ReadNode;
import compiler.AST.Nodes.ReturnNode;
import compiler.AST.Nodes.VarAssignNode;
import compiler.AST.Nodes.VarDeclareNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.ArrType;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.IntType;
import compiler.AST.Types.PairType;
import compiler.AST.Types.Type;
import compiler.SubRoutines;
import compiler.instr.ADD;
import compiler.instr.AND;
import compiler.instr.BL;
import compiler.instr.CMP;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.LDR_COND.COND;
import compiler.instr.MOV;
import compiler.instr.MUL;
import compiler.instr.ORR;
import compiler.instr.Operand.Addr;
import compiler.instr.Operand.Imm_INT;
import compiler.instr.Operand.Imm_INT_MEM;
import compiler.instr.Operand.Imm_STRING;
import compiler.instr.Operand.Imm_STRING_MEM;
import compiler.instr.Operand.Operand;
import compiler.instr.Operand.Reg_Shift;
import compiler.instr.POP;
import compiler.instr.PUSH;
import compiler.instr.REG;
import compiler.instr.RS;
import compiler.instr.SECTION;
import compiler.instr.STR;
import compiler.instr.STRING_FIELD;
import compiler.instr.SUB;
import compiler.instr.Shift;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ASTVisitor {

  private static final int WORD_SIZE = 4;
  private static final int SHIFT_TIMES_4 = 2;
  private static final int BYTE = 1;


  private static List<Instr> instructions;
  private static List<Instr> data;
  private static List<REG> availableRegs;
  private Set<String> specialLabels;
  private SymbolTable currentST;
  private int totalStackOffset;
  private int nextPosInStack;
  private int offsetFromInitialSP;

  public ASTVisitor() {
    instructions = new ArrayList<>();
    data = new ArrayList<>();
    this.specialLabels = new LinkedHashSet<>();
    availableRegs = new ArrayList<>(allUsableRegs);
    offsetFromInitialSP = 0;
    new SubRoutines(instructions);
  }

  public static int toInt(String s) {
    return Integer.parseInt(s);
  }

  public static String addStringField(String string) {
    String labelName = "msg_" + (data.size() / 2);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }

  public static void jumpToFunctionLabel(String label) {
    // todo deprecated
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

  private static List<REG> getUsedRegs() {
    List<REG> regs = new ArrayList<>(allUsableRegs);
    regs.removeAll(availableRegs);
    return regs;
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
        return new ADD(rd, rn, op2, false);
      case "sub":
        return new SUB(rd, rn, op2, false);
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
    freeReg(rd);
  }

  public CodeGenData visitUnaryExpr(UnaryExpr expr) {

    if ((expr.insideExpr() instanceof IntExpr) // Set int value to negative
      && expr.operator() == UNOP.MINUS) {
      ((IntExpr) expr.insideExpr()).setNegative();
      return visit(expr.insideExpr());
    } else if (expr.insideExpr() instanceof Ident
      && expr.operator() == UNOP.MINUS) {
      REG rd = (REG) visit(expr.insideExpr());
      specialLabels.addAll(
        Arrays.asList("p_throw_overflow_error", "p_throw_runtime_error",
          "p_print_string"));
      instructions.add(new RS(rd, rd, new Imm_INT(0), "BS"));
      instructions.add(new BL("p_throw_overflow_error", COND.VS));
      return rd;
    }

    //TODO: handle other types
    return null;
  }

  public CodeGenData visitIntExpr(IntExpr expr) {
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_INT_MEM(toInt(expr.value())), false));
    return rd;
  }

  public CodeGenData visitBinaryExp(BinExpr binExpr) {
    REG rd = (REG) visit(binExpr.lhs());

    REG rn;
    if (rd == R10) { // push rn to avoid running out of registers
      instructions.add(new PUSH(rd));
      freeReg(rd);
      rn = (REG) visit(binExpr.rhs());
      rd = useAvailableReg();
      instructions.add(new POP(rd));
    } else {
      rn = (REG) visit(binExpr.rhs());
    }

    switch (binExpr.operator()) {
      case OR:
        instructions.add(new ORR(rd, rd, rn));
        break;
      case AND:
        instructions.add(new AND(rd, rd, rn));
        break;
      case PLUS:
        specialLabels.addAll(
          Arrays.asList("p_throw_overflow_error", "p_throw_runtime_error",
            "p_print_string"));
        instructions.add(new ADD(rd, rd, rn, true));
        instructions.add(new BL("p_throw_overflow_error", COND.VS));
        break;
      case MINUS:
        specialLabels.addAll(Arrays
          .asList("p_throw_overflow_error", "p_throw_runtime_error",
            "p_print_string"));
        instructions.add(new SUB(rd, rd, rn, true));
        instructions.add(new BL("p_throw_overflow_error", COND.VS));
        break;
      case MUL:
        specialLabels.addAll(Arrays
          .asList("p_throw_overflow_error", "p_throw_runtime_error",
            "p_print_string"));

        instructions.add(new MUL(rd, rn, rd, rn));
        instructions.add(new CMP(rn, rd, new Shift(ASR, 31)));
        instructions.add(new BL("p_throw_overflow_error", COND.NE));
        break;
      case DIV:
        specialLabels.addAll(Arrays
          .asList("p_check_divide_by_zero", "p_throw_runtime_error",
            "p_print_string"));

        instructions.addAll(Arrays
          .asList(new MOV(R0, rd), new MOV(R1, rn),
            new BL("p_check_divide_by_zero"),
            new BL("__aeabi_idiv"), new MOV(rd, R0)));
        break;
      case MOD:
        specialLabels.addAll(Arrays
          .asList("p_check_divide_by_zero", "p_throw_runtime_error",
            "p_print_string"));

        instructions.addAll(Arrays
          .asList(new MOV(R0, rd), new MOV(R1, rn),
            new BL("p_check_divide_by_zero"),
            new BL("__aeabi_idivmod"), new MOV(rd, R1)));
        break;

      case EQUAL:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1), COND.EQ),
            new MOV(rd, new Imm_INT(0), COND.NE)));
        break;

      case GE:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1),
              COND.GE),
            new MOV(rd, new Imm_INT(0), COND.LT)));
        break;

      case GT:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1),
              COND.GT),
            new MOV(rd, new Imm_INT(0), COND.LE)));
        break;

      case LE:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1),
              COND.LE),
            new MOV(rd, new Imm_INT(0), COND.GT)));
        break;

      case LT:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1),
              COND.LT),
            new MOV(rd, new Imm_INT(0), COND.GE)));
        break;

      case NOTEQUAL:
        instructions.addAll(Arrays
          .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1),
              COND.NE),
            new MOV(rd, new Imm_INT(0), COND.EQ)));
        break;

    }
    freeReg(rn);
    return rd;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode) {
    REG rd = (REG) visit(printNode.expr());
    if (printNode.expr() instanceof ArrayElem) {
      instructions.add(new LDR(rd, new Addr(rd),
        printNode.expr().sizeOf() == 1));
    }

    // mov result into arg register
    instructions.add(new MOV(R0, rd));

    if (printNode.expr().type().equals(CharType.getInstance())) {
      instructions.add(new BL("putchar"));
    } else if (printNode.expr().type().equals(IntType.getInstance())) {
      instructions.add(new BL("p_print_int"));
      specialLabels.add("p_print_int");
    } else if (printNode.expr().type().equals(BoolType.getInstance())) {
      instructions.add(new BL("p_print_bool"));
      specialLabels.add("p_print_bool");
    } else if (printNode.expr().type().equals(ArrType.stringType())) {
      instructions.add(new BL("p_print_string"));
      specialLabels.add("p_print_string");
    } else {
      instructions.add(new BL("p_print_reference"));
      specialLabels.add("p_print_reference");
    }

    if (printNode.newLine()) {
      instructions.add(new BL("p_print_ln"));
      specialLabels.add("p_print_ln");
    }
    freeReg(rd);
    return null;
  }

  public CodeGenData visitReadExpr(ReadNode readNode) {
    //REG rd = (REG) visit((ASTData) readNode.lhs());

    //In this case we don't visit the Node because we don't want to store the value but the address
    REG rd = useAvailableReg();
    instructions.add(new ADD(rd, SP, new Imm_INT(
      currentST.lookUpAllVar(readNode.lhs().varName()).getStackOffset()
        - offsetFromInitialSP),
      false));

    instructions.add(new MOV(R0, rd));

    if ((readNode.lhs()).type().equals(IntType.getInstance())) {
      instructions.add(new BL("p_read_int"));
      specialLabels.add("p_read_int");
    } else if ((readNode.lhs()).type()
      .equals(CharType.getInstance())) {
      instructions.add(new BL("p_read_char"));
      specialLabels.add("p_read_char");
    }
    freeReg(rd);
    return null;
  }

  public CodeGenData visitStringExpr(StringExpr stringExpr) {
    String labelName = addStringField(stringExpr.getValue());
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_STRING_MEM(labelName), false));
    return rd;
  }

  public CodeGenData visitCharExpr(CharExpr charExpr) {
    REG rd = useAvailableReg();
    instructions
      .add(new MOV(rd, !(charExpr.isEscapeChar()) ? new Imm_STRING("'" + charExpr.value() + "'")
        : new Imm_INT(Integer.parseInt(charExpr.value()))));
    return rd;
  }

  public CodeGenData visitBoolExpr(BoolExpr boolExpr) {
    REG rd = useAvailableReg();
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
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack,
      false);
    freeReg(rd);
    return null;
  }

  public CodeGenData visitAssignNode(VarAssignNode varAssignNode) {
    //TODO: refactor concat in one
    if (varAssignNode.lhs() instanceof Ident) {
      visitIdentAssign(varAssignNode);
    }
    if (varAssignNode.lhs() instanceof ArrayElem) {
      visitArrayAssign(varAssignNode);
    }
    if (varAssignNode.lhs() instanceof PairElem) {
      visitPairAssign(varAssignNode);
    }
    return null;
  }

  private CodeGenData visitPairAssign(VarAssignNode varAssignNode) {
    REG rd = (REG) visit(varAssignNode.rhs());
    REG rn = (REG) visitHeapAlloc(varAssignNode.lhs());
    saveVarData(varAssignNode.lhs().type(), rd, rn, 0, false);
    freeReg(rd);
    freeReg(rn);
    return null;
  }

  private CodeGenData visitIdentAssign(VarAssignNode varAssignNode) {
    //TODO: CONSIDER other cases such as Arrays or Pairs
    REG rd = (REG) visit(varAssignNode.rhs());
    int offset = currentST.lookUpAllVar(varAssignNode.lhs().varName())
      .getStackOffset() - offsetFromInitialSP;
    saveVarData(varAssignNode.rhs().type(), rd, SP, offset, false);
    freeReg(rd);
    return null;
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
    Expr[] array = ((ArrayLiter) varDeclareNode.rhs()).elems();
    int size = array.length;
    // malloc the number of elems plus one
    // to hold the size of the array for runtime errors
    setArg(new Imm_INT_MEM((size + 1) * WORD_SIZE));
    instructions.add(new BL("malloc"));
    REG arrAddress = useAvailableReg();
    instructions.add(new MOV(arrAddress, R0));
    for (int i = 0; i < size; i++) { // store array elements
      storeArrayElem(array[i], arrAddress, (i + 1) * WORD_SIZE);
    }
    // store size of the array
    REG sizeReg = useAvailableReg();
    instructions.add(new LDR(sizeReg, new Imm_INT_MEM(size), false));
    instructions
      .add(new STR(sizeReg, new Addr(arrAddress, true, null), false, false));

    // set sp at the array's address
    instructions
      .add(new STR(arrAddress, new Addr(SP, true, null), false, false));
    nextPosInStack -= 4;
    currentST.lookUpAllVar(varDeclareNode.varName())
      .setStackOffset(nextPosInStack);
    freeReg(sizeReg);
    freeReg(arrAddress);
    return null;
  }

  private void storeArrayElem(Expr expr, REG objectAddr, int offset) {
    REG rd = (REG) visit(expr);
    saveVarData(expr.type(), rd, objectAddr, offset, false);
    freeReg(rd);
  }

  public CodeGenData visitArrayAssign(VarAssignNode varAssignNode) {
    REG rd = (REG) visit(varAssignNode.rhs());  // get new value
    REG rn = (REG) visit(varAssignNode.lhs());  // get array data
    instructions.add(new STR(rd, new Addr(rn)));
    freeReg(rn);
    freeReg(rd);
    return null;
  }

  public CodeGenData visitArrayElem(ArrayElem arrayElem) {
    REG arrAddress = loadVar(arrayElem.varName(), false);
    REG index = (REG) visit(arrayElem.indexes()[0]); // simple array case
    setArgs(new REG[]{index, arrAddress});
    specialLabels.addAll(Arrays.asList(
      "p_check_array_bounds", "p_throw_runtime_error", "p_print_string"));
    instructions.addAll(Arrays.asList(
      new BL("p_check_array_bounds"),
      new ADD(arrAddress, arrAddress, new Imm_INT(WORD_SIZE)),
      new ADD(arrAddress, arrAddress, new Reg_Shift(index,
        new Shift(LSL, SHIFT_TIMES_4)))));
    freeReg(index);
    return arrAddress;
  }

  private CodeGenData visitPairDeclare(VarDeclareNode varDeclareNode) {
    REG rd = (REG) visit(varDeclareNode.rhs());
    nextPosInStack -= WORD_SIZE;
    currentST.lookUpAllVar(varDeclareNode.varName()).setStackOffset(nextPosInStack);
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack,
      false);
    freeReg(rd);
    return null;
  }

  public CodeGenData visitPair(Pair pair) {
    setArg(new Imm_INT_MEM(2 * WORD_SIZE));
    instructions.add(new BL("malloc"));
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, R0)); // fetch address of pair
    storeExpInHeap(pair.fst(), rd, 0);
    storeExpInHeap(pair.snd(), rd, 4);
    return rd;
  }

  public CodeGenData visitNullPair() {
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_INT_MEM(0), false));
    return rd;
  }

  private void storeExpInHeap(Expr expr, REG objectAddr, int offset) {
    REG rd = (REG) visit(expr);
    setArg(new Imm_INT_MEM(expr.sizeOf()));
    instructions.add(new BL("malloc"));
    saveVarData(expr.type(), rd, R0, 0, false);
    instructions.add(new STR(R0, new Addr(objectAddr, true, new Imm_INT(offset))));
    freeReg(rd);
  }

  private void setArg(Operand op2) {
    instructions.add(new LDR(R0, op2, false));
  }

  private void setArgs(Operand[] ops) {
    for (int i = 0; i < ops.length && i < 4; i++) {
      instructions.add(new MOV(REG.values()[i], ops[i]));
    }
  }

  private REG topRegAvailable() {
    return availableRegs.get(0);
  }

  private REG useAvailableReg() {
    return availableRegs.remove(0);
  }

  private void freeReg(REG reg) {
    if (allUsableRegs.contains(reg)) {
      availableRegs.add(0, reg);
    }
  }

  public CodeGenData visitIdent(Ident ident) {
    return loadVar(ident.varName(), ident.sizeOf() == 1);
  }

  private REG loadVar(String varName, boolean isByteInstr) {
    REG rd = useAvailableReg();
    int offset = currentST.lookUpAllVar(varName).getStackOffset()
      - offsetFromInitialSP;
    instructions.add(new LDR(rd,
      new Addr(SP, true, new Imm_INT(offset)), isByteInstr));
    return rd;
  }

  public CodeGenData visitFuncNode(FuncNode funcNode) {
    currentST = funcNode.symbolTable();
    instructions.add(new LABEL("f_" + funcNode.name()));
    instructions.add(new PUSH(LR));
    if (!funcNode.paramList().exprList().isEmpty()) {
      visit(funcNode.paramList());
    }
    funcNode.getParentNode().children().forEach(this::visit);
    instructions.addAll(Arrays.asList(new POP(PC), new POP(PC)));
    instructions.add(new SECTION("ltorg"));
    currentST = currentST.getEncSymTable();
    return null;
  }

  public CodeGenData visitReturn(ReturnNode returnNode) {
    REG rd = (REG) visit(returnNode.expr());
    instructions.add(new MOV(R0, rd));
    freeReg(rd);
    return null;
  }

  public CodeGenData visitFuncCall(FuncCall funcCall) {
    visit(funcCall.argsList());
    instructions.add(new BL("f_" + funcCall.funcName()));
    offsetFromInitialSP = 0;
    instructions.add(
      new ADD(SP, SP, new Imm_INT(funcCall.argsList().bytesPushed()), false));
    REG rd = useAvailableReg();
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
      int offsetFromBase =
        (!(e.type() instanceof CharType) && !(e.type() instanceof BoolType))
          ? -WORD_SIZE : -BYTE;
      offsetFromInitialSP += offsetFromBase;
      saveVarData(e.type(), rd, SP, offsetFromBase,
        true);
      freeReg(rd);
    }
    return null;
  }

  public CodeGenData visitPairExpr(PairElem pairElem) {
    specialLabels
      .addAll(Arrays.asList("p_check_null_pointer", "p_throw_runtime_error", "p_print_string"));
    REG rd = (REG) visit(pairElem.expr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("p_check_null_pointer"));
    instructions
      .add(new LDR(rd, new Addr(rd, true, new Imm_INT(pairElem.posInPair() * WORD_SIZE)), false));
    instructions.add(new LDR(rd, new Addr(rd), false));
    return rd;
  }

  private Operand visitHeapAlloc(LHS pairElem) {
    specialLabels
      .addAll(Arrays.asList("p_check_null_pointer", "p_throw_runtime_error", "p_print_string"));
    REG rd = (REG) visit(((PairElem) pairElem).expr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("p_check_null_pointer"));
    instructions.add(
      new LDR(rd, new Addr(rd, true, new Imm_INT(((PairElem) pairElem).posInPair() * WORD_SIZE)),
        false));
    return rd;
  }

  public CodeGenData visitFreeNode(FreeNode freeNode) {
    specialLabels.addAll(Arrays
      .asList("p_free_pair", "p_throw_runtime_error",
        "p_print_string"));
    REG rd = (REG) visit(freeNode.freeExpr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("p_free_pair"));
    freeReg(rd);
    return null;
  }
}
