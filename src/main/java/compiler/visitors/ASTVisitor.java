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

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.LHS.ArrayElemLHS;
import compiler.AST.NodeElements.LHS.PairElemLHS;
import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.ArrayElemRHS;
import compiler.AST.NodeElements.RHS.ArrayLiter;
import compiler.AST.NodeElements.RHS.BinExpr;
import compiler.AST.NodeElements.RHS.BoolExpr;
import compiler.AST.NodeElements.RHS.CharExpr;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.NodeElements.RHS.FuncCall;
import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.NodeElements.RHS.Pair;
import compiler.AST.NodeElements.RHS.PairElemRHS;
import compiler.AST.NodeElements.RHS.StringExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.FreeNode;
import compiler.AST.Nodes.FuncNode;
import compiler.AST.Nodes.IfElseNode;
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
import compiler.AST.Types.Type;
import compiler.SubRoutines;
import compiler.instr.ADD;
import compiler.instr.AND;
import compiler.instr.B;
import compiler.instr.CMP;
import compiler.instr.EOR;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.LDR.COND;
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
  private static final int BYTE_SIZE = 1;

  private static List<Instr> instructions;
  private static List<Instr> data;
  private static List<REG> availableRegs;
  private Set<String> specialLabels;
  private SymbolTable currentST;
  private int totalStackOffset;
  private int scopeStackOffset;
  private int nextPosInStack;
  private int branchNb = 0;

  public ASTVisitor() {
    instructions = new ArrayList<>();
    data = new ArrayList<>();
    this.specialLabels = new LinkedHashSet<>();
    availableRegs = new ArrayList<>(allUsableRegs);
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

  public List<Instr> generate(AST root) {
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

  public CodeGenData visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
    return null;
  }

  private void configureStack(String type) {
    int maxIntImmShift = 1024;
    if (scopeStackOffset > 0) {
      int temp = scopeStackOffset;
      while (temp / 1024 != 0) {
        instructions.add(buildInstr(type, new Imm_INT(maxIntImmShift)));
        temp = temp - 1024;
      }
      instructions.add(buildInstr(type,
          new Imm_INT(scopeStackOffset % maxIntImmShift)));
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

  private void constructEndProgram() {
    configureStack("add");
    setArg(new Imm_INT_MEM(toInt("0")), false);
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
    instructions.add(new B("exit", true));
    freeReg(rd);
  }

  public CodeGenData visitUnaryExpr(UnaryExpr expr) {

    if ((expr.insideExpr() instanceof IntExpr) // Set int value to negative
        && expr.operator() == UNOP.MINUS) {
      ((IntExpr) expr.insideExpr()).setNegative();
      return visit(expr.insideExpr());
    }

    REG rd = (REG) visit(expr.insideExpr());
    switch (expr.operator()) {
      case MINUS:
        specialLabels.addAll(
            Arrays.asList("p_throw_overflow_error", "p_throw_runtime_error",
                "p_print_string"));
        instructions.add(new RS(rd, rd, new Imm_INT(0), "BS"));
        instructions.add(new B("p_throw_overflow_error", true, COND.VS));
        break;
      case NEG:
        instructions.add(new EOR(rd, rd, new Imm_INT(1)));
        break;
      case LEN:
        int offset =
            currentST.getTotalOffset(((Ident) expr.insideExpr()).varName());
        instructions.add(new LDR(rd, new Addr(SP, true,
            new Imm_INT(offset))));  //load address of array into rd
        instructions.add(new LDR(rd, new Addr(
            rd))); //load first element at this address, which is the size
        break;
      default:
        break;
    }
    return rd;
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

    BinExpr.BINOP operator = binExpr.operator();

    switch (operator) {
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
        instructions.add(new B("p_throw_overflow_error", true, operator.cond()));
        break;
      case MINUS:
        specialLabels.addAll(Arrays
            .asList("p_throw_overflow_error", "p_throw_runtime_error",
                "p_print_string"));
        instructions.add(new SUB(rd, rd, rn, true));
        instructions.add(new B("p_throw_overflow_error", true, operator.cond()));
        break;
      case MUL:
        specialLabels.addAll(Arrays
            .asList("p_throw_overflow_error", "p_throw_runtime_error",
                "p_print_string"));

        instructions.add(new MUL(rd, rn, rd, rn));
        instructions.add(new CMP(rn, rd, new Shift(ASR, 31)));
        instructions.add(new B("p_throw_overflow_error", true, operator.cond()));
        break;
      case DIV:
        specialLabels.addAll(Arrays
            .asList("p_check_divide_by_zero", "p_throw_runtime_error",
                "p_print_string"));

        instructions.addAll(Arrays
            .asList(new MOV(R0, rd), new MOV(R1, rn),
                new B("p_check_divide_by_zero", true),
                new B("__aeabi_idiv", true), new MOV(rd, R0)));
        break;
      case MOD:
        specialLabels.addAll(Arrays
            .asList("p_check_divide_by_zero", "p_throw_runtime_error",
                "p_print_string"));

        instructions.addAll(Arrays
            .asList(new MOV(R0, rd), new MOV(R1, rn),
                new B("p_check_divide_by_zero", true),
                new B("__aeabi_idivmod", true), new MOV(rd, R1)));
        break;

      case EQUAL:
      case GE:
      case GT:
      case LE:
      case LT:
      case NOTEQUAL:
        instructions.addAll(Arrays
            .asList(new CMP(rd, rn, null), new MOV(rd, new Imm_INT(1), operator.cond()),
                new MOV(rd, new Imm_INT(0), BinExpr.BINOP.opposites().get(operator).cond())));
        break;

    }
    freeReg(rn);
    return rd;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode) {
    REG rd = (REG) visit(printNode.expr());

    // mov result into arg register
    setArg(rd, true);

    if (printNode.expr().type().equals(CharType.getInstance())) {
      instructions.add(new B("putchar", true));
    } else if (printNode.expr().type().equals(IntType.getInstance())) {
      instructions.add(new B("p_print_int", true));
      specialLabels.add("p_print_int");
    } else if (printNode.expr().type().equals(BoolType.getInstance())) {
      instructions.add(new B("p_print_bool", true));
      specialLabels.add("p_print_bool");
    } else if (printNode.expr().type().equals(ArrType.stringType())) {
      instructions.add(new B("p_print_string", true));
      specialLabels.add("p_print_string");
    } else {
      instructions.add(new B("p_print_reference", true));
      specialLabels.add("p_print_reference");
    }

    if (printNode.newLine()) {
      instructions.add(new B("p_print_ln", true));
      specialLabels.add("p_print_ln");
    }
    freeReg(rd);
    return null;
  }

  public CodeGenData visitReadExpr(ReadNode readNode) {
    REG rd = (REG) visit(readNode.lhs());

    setArg(rd, true);
    if ((readNode.lhs()).type().equals(IntType.getInstance())) {
      instructions.add(new B("p_read_int", true));
      specialLabels.add("p_read_int");
    } else if ((readNode.lhs()).type()
        .equals(CharType.getInstance())) {
      instructions.add(new B("p_read_char", true));
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
        .add(new MOV(rd, !(charExpr.isEscapeChar()) ? new Imm_STRING(
            "'" + charExpr.value() + "'")
            : new Imm_INT(Integer.parseInt(charExpr.value()))));
    return rd;
  }

  public CodeGenData visitBoolExpr(BoolExpr boolExpr) {
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, new Imm_INT(boolExpr.value() ? 1 : 0)));
    return rd;
  }

  public CodeGenData visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    REG rd = (REG) visit(varDeclareNode.rhs());
    nextPosInStack -=
        isByteSize(varDeclareNode.varType()) ? BYTE_SIZE : WORD_SIZE;

    // store variable in the stack and save offset in symbol table
    currentST.lookUpAllVar(varDeclareNode.varName())
        .setLocalOffset(nextPosInStack);
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack, false);

    freeReg(rd);
    return null;
  }

  public CodeGenData visitAssignNode(VarAssignNode varAssignNode) {
    REG rd = (REG) visit(varAssignNode.rhs());
    REG rn = (REG) visit(varAssignNode.lhs());

    saveVarData(varAssignNode.lhs().type(), rd, rn, 0, false);

    freeReg(rd);
    freeReg(rn);

    return null;
  }

  public CodeGenData visitArrayLiter(ArrayLiter arrayLiter) {
    REG arrAddress = useAvailableReg();
    Expr[] array = arrayLiter.elems();
    int elemSize = 0;
    int size = array.length;

    if (size > 0) {
      elemSize = isByteSize(((ArrType) arrayLiter.type()).getArrayElem())
          ? BYTE_SIZE : WORD_SIZE;
    }

    // malloc the number of elements plus one for to hold the size
    setArg(new Imm_INT_MEM(size * elemSize + WORD_SIZE), false);
    instructions.add(new B("malloc", true));
    instructions.add(new MOV(arrAddress, R0));

    // store array address elements in the heap
    for (int i = 0; i < size; i++) {
      storeArrayElem(array[i], arrAddress, WORD_SIZE + i * elemSize);
    }

    // store size of the array in the heap
    REG sizeReg = useAvailableReg();
    instructions.add(new LDR(sizeReg, new Imm_INT_MEM(size)));
    instructions.add(new STR(sizeReg, new Addr(arrAddress)));

    freeReg(sizeReg);
    return arrAddress;
  }

  private CodeGenData saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    instructions
        .add(new STR(rd, new Addr(rn, true, new Imm_INT(offset)),
            isByteSize(varType),
            update));
    return null;
  }

  private void storeArrayElem(Expr expr, REG objectAddr, int offset) {
    REG rd = (REG) visit(expr);
    saveVarData(expr.type(), rd, objectAddr, offset, false);
    freeReg(rd);
  }

  public CodeGenData visitArrayAssign(VarAssignNode varAssignNode) {
    REG rd = (REG) visit(varAssignNode.rhs());  // get new value
    REG rn = (REG) visit(varAssignNode.lhs());  // get array elem address

    instructions.add(new STR(rd, new Addr(rn),
        isByteSize(varAssignNode.rhs().type())));

    freeReg(rn);
    freeReg(rd);
    return null;
  }

  public CodeGenData visitArrayElemLHS(ArrayElemLHS arrayElemLHS) {
    return visitArrayElem(arrayElemLHS);
  }

  public CodeGenData visitArrayElemRHS(ArrayElemRHS arrayElemRHS) {
    REG rd = (REG) visitArrayElem(arrayElemRHS);
    instructions.add(new LDR(rd, new Addr(rd))); // load value of array elem
    return rd;
  }


  // returns address of array elem
  public CodeGenData visitArrayElem(ArrayElem arrayElem) {
    REG arrAddress = loadFromStack(arrayElem.varName());
    REG index;

    for (Expr indexExpr : arrayElem.indexes()) {
      index = (REG) visit(indexExpr); // simple array case
      instructions.add(new LDR(arrAddress, new Addr(arrAddress)));
      setArgs(new REG[]{index, arrAddress});
      specialLabels.addAll(Arrays.asList(
          "p_check_array_bounds", "p_throw_runtime_error", "p_print_string"));
      instructions.add(new B("p_check_array_bounds", true));
      instructions.add(new ADD(arrAddress, arrAddress, new Imm_INT(WORD_SIZE)));

      if (isByteSize(arrayElem.type())) {
        instructions.add(new ADD(arrAddress, arrAddress, index));
      } else {
        instructions.add(new ADD(arrAddress, arrAddress, new Reg_Shift(index,
            new Shift(LSL, SHIFT_TIMES_4))));
      }

      freeReg(index);
    }
    return arrAddress;
  }

  public CodeGenData visitPairElemLHS(PairElemLHS pairElemLHS) {
    return visitPairElem(pairElemLHS);
  }

  public CodeGenData visitPairElemRHS(PairElemRHS pairElemRHS) {
    REG rd = (REG) visitPairElem(pairElemRHS);
    instructions.add(new LDR(rd, new Addr(rd)));
    return rd; // returns value of pair element
  }

  // returns address of pair element
  public CodeGenData visitPairElem(PairElem pairElem) {
    specialLabels.addAll(Arrays.asList(
        "p_check_null_pointer",
        "p_throw_runtime_error",
        "p_print_string"));
    REG rd = (REG) visit(pairElem.expr());
    setArg(rd, true);
    instructions.add(new B("p_check_null_pointer", true));
    instructions.add(new LDR(rd,
        new Addr(rd, true, new Imm_INT(pairElem.posInPair() * WORD_SIZE)),
        false));
    return rd;
  }

  public CodeGenData visitPair(Pair pair) {
    setArg(new Imm_INT_MEM(2 * WORD_SIZE), false);
    instructions.add(new B("malloc", true));
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
    setArg(new Imm_INT_MEM(expr.sizeOf()), false);
    instructions.add(new B("malloc", true));
    saveVarData(expr.type(), rd, R0, 0, false);
    instructions
        .add(new STR(R0, new Addr(objectAddr, true, new Imm_INT(offset))));
    freeReg(rd);
  }

  private void setArg(Operand op2, boolean isAddress) {
    if (isAddress) {
      instructions.add(new MOV(R0, op2));
    } else {
      instructions.add(new LDR(R0, op2, false));
    }
  }

  private void setArgs(Operand[] ops) {
    for (int i = 0; i < ops.length && i < 4; i++) {
      instructions.add(new MOV(REG.values()[i], ops[i]));
    }
  }

  private REG useAvailableReg() {
    return availableRegs.remove(0);
  }

  private void freeReg(REG reg) {
    if (allUsableRegs.contains(reg)) {
      availableRegs.add(0, reg);
    }
  }

  public CodeGenData visitIdentLHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(ident.varName());
    instructions.add(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  public CodeGenData visitIdentRHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(ident.varName());
    instructions.add(new LDR(rd, new Addr(SP, true, new Imm_INT(offset)),
        isByteSize(ident.type())));
    return rd;
  }

  private REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    instructions.add(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  public CodeGenData visitFuncNode(FuncNode funcNode) {
    enterScope(funcNode.symbolTable());
    instructions.add(new LABEL("f_" + funcNode.name()));
    instructions.add(new PUSH(LR));
    configureStack("sub");
    if (!funcNode.paramList().exprList().isEmpty()) {
      visit(funcNode.paramList());
    }
    visit(funcNode.getParentNode());
    instructions.add(new POP(PC));
    instructions.add(new SECTION("ltorg"));
    exitScope(currentST.getEncSymTable());
    return null;
  }

  public CodeGenData visitReturn(ReturnNode returnNode) {
    REG rd = (REG) visit(returnNode.expr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new ADD(SP, SP, new Imm_INT(totalStackOffset)));
    instructions.add(new POP(PC));
    freeReg(rd);
    return null;
  }

  public CodeGenData visitFuncCall(FuncCall funcCall) {
    visit(funcCall.argsList());
    instructions.add(new B("f_" + funcCall.funcName(), true));
    instructions.add(
        new ADD(SP, SP, new Imm_INT(funcCall.argsList().bytesPushed()), false));
    currentST.decrementStackOffset(funcCall.argsList().bytesPushed());
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, R0));
    return rd;
  }

  public CodeGenData visitParams(ListExpr listExpr) {
    List<String> paramNames = listExpr.paramNames();
    List<Expr> exprList = listExpr.exprList();
    // LR has been pushed and we have some space for the variable declaration
    // the arguments reside at this offset from the current stack pointer
    int offset = WORD_SIZE + scopeStackOffset;
    for (int i = 0; i < exprList.size(); i++) {
      currentST.lookUpVarScope(paramNames.get(i)).setLocalOffset(offset);
      offset += exprList.get(i).sizeOf();
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
              ? -WORD_SIZE : -BYTE_SIZE;
      currentST.incrementStackOffset(-offsetFromBase);
      saveVarData(e.type(), rd, SP, offsetFromBase,
          true);
      freeReg(rd);
    }
    return null;
  }

  public CodeGenData visitFreeNode(FreeNode freeNode) {
    specialLabels.addAll(Arrays
        .asList("p_free_pair", "p_throw_runtime_error",
            "p_print_string"));
    REG rd = (REG) visit(freeNode.freeExpr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new B("p_free_pair", true));
    freeReg(rd);
    return null;
  }

  public CodeGenData visitIfElseNode(IfElseNode ifElseNode) {
    REG rd = (REG) visit(ifElseNode.cond());
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
    return null;
  }

  public CodeGenData visitWhileNode(WhileNode whileNode) {
    instructions.add(new B("L" + branchNb));

    int condBranchNb = branchNb;
    branchNb += 2;
    // Add label for do statement
    instructions.add(new LABEL("L" + (condBranchNb + 1)));

    visitChildStats(whileNode.statST(), whileNode.parentNode());

    // Add label for condition
    instructions.add(new LABEL("L" + condBranchNb));
    REG rd = (REG) visit(whileNode.condition());
    instructions.add(new CMP(rd, new Imm_INT(1)));
    instructions.add(new B("L" + (condBranchNb + 1), COND.EQ));
    freeReg(rd);
    return null;
  }

  public CodeGenData visitNewScope(ScopeNode scopeNode) {
    visitChildStats(scopeNode.symbolTable(), scopeNode.parentNode());
    return null;
  }

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
    totalStackOffset = scopeStackOffset + tempValues[0];
    scopeStackOffset = scopeOffset;
    nextPosInStack = scopeStackOffset;
    return tempValues;
  }

  private void enterScope(SymbolTable symbolTable) {
    currentST = symbolTable;
  }

  private void exitScope(SymbolTable encSymTable) {
    currentST = encSymTable;
  }

  public static boolean isByteSize(Type type) {
    return type.equals(CharType.getInstance())
        || type.equals(BoolType.getInstance());
  }
}
