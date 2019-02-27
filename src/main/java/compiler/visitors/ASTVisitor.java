package compiler.visitors;

import static compiler.IR.Operand.REG.*;
import static compiler.IR.Operand.Shift.SHIFT_TYPE.*;

import compiler.AST.ASTData;
import compiler.AST.NodeElements.*;
import compiler.AST.NodeElements.LHS.*;
import compiler.AST.NodeElements.RHS.*;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.*;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.*;
import compiler.IR.Instructions.B;
import compiler.IR.Instructions.CMP;
import compiler.IR.Instructions.EOR;
import compiler.IR.Instructions.Instr;
import compiler.IR.Instructions.LABEL;
import compiler.IR.Instructions.LDR;
import compiler.IR.Instructions.MOV;
import compiler.IR.Instructions.MUL;
import compiler.IR.Instructions.ORR;
import compiler.IR.Instructions.POP;
import compiler.IR.Instructions.PUSH;
import compiler.IR.Operand.REG;
import compiler.IR.Instructions.RS;
import compiler.IR.Instructions.SECTION;
import compiler.IR.Instructions.STR;
import compiler.IR.Instructions.SUB;
import compiler.IR.SubRoutines;
import compiler.IR.Instructions.ADD;
import compiler.IR.Instructions.AND;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Operand.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ASTVisitor {

  public static final int WORD_SIZE = 4;
  public static final int SHIFT_TIMES_4 = 2;
  public static final int BYTE_SIZE = 1;

  private List<Instr> instructions; // list of ARM instructions
  // handles adding subroutines and its data fields
  private SubRoutines subroutines;

  private List<REG> availableRegs;
  private SymbolTable currentST;
  private int totalStackOffset;
  private int scopeStackOffset;
  private int nextPosInStack;
  private int branchNb = 0;

  public ASTVisitor() {
    instructions = new ArrayList<>();
    subroutines = new SubRoutines();
    availableRegs = new ArrayList<>(allUsableRegs);
  }

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

  private REG visit(ASTData data) {
    return data.accept(this);
  }

  public REG visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
    return null;
  }

  public REG visitVarDeclareNode(VarDeclareNode varDeclareNode) {
    REG rd = visit(varDeclareNode.rhs());
    nextPosInStack -=
        isByteSize(varDeclareNode.varType()) ? BYTE_SIZE : WORD_SIZE;

    // store variable in the stack and save offset in symbol table
    currentST.lookUpAllVar(varDeclareNode.varName())
        .setLocalOffset(nextPosInStack);
    saveVarData(varDeclareNode.varType(), rd, SP, nextPosInStack, false);

    freeReg(rd);
    return null;
  }

  public REG visitAssignNode(VarAssignNode varAssignNode) {
    REG rd = visit(varAssignNode.rhs());
    REG rn = visit(varAssignNode.lhs());

    saveVarData(varAssignNode.lhs().type(), rd, rn, 0, false);

    freeReg(rd);
    freeReg(rn);

    return null;
  }

  public REG visitExitNode(ExitNode exitNode) {
    REG rd = visit(exitNode.exitStatus());
    instructions.add(new MOV(R0, rd));
    instructions.add(new B("exit", true));
    freeReg(rd);
    return null;
  }

  public REG visitPrintNode(PrintNode printNode) {
    REG rd = visit(printNode.expr());

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
    return null;
  }

  public REG visitReadNode(ReadNode readNode) {
    REG rd = visit(readNode.lhs());
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
    return null;
  }

  public REG visitFuncNode(FuncNode funcNode) {
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

  public REG visitFreeNode(FreeNode freeNode) {
    REG rd = visit(freeNode.freeExpr());
    instructions.add(new MOV(R0, rd));
    String freePairLabel = subroutines.addFreePairCheck();
    instructions.add(new B(freePairLabel, true));
    freeReg(rd);
    return null;
  }

  public REG visitIfElseNode(IfElseNode ifElseNode) {
    REG rd = visit(ifElseNode.cond());
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

  public REG visitWhileNode(WhileNode whileNode) {
    instructions.add(new B("L" + branchNb));

    int condBranchNb = branchNb;
    branchNb += 2;
    // Add label for do statement
    instructions.add(new LABEL("L" + (condBranchNb + 1)));

    visitChildStats(whileNode.statST(), whileNode.parentNode());

    // Add label for condition
    instructions.add(new LABEL("L" + condBranchNb));
    REG rd = visit(whileNode.condition());
    instructions.add(new CMP(rd, new Imm_INT(1)));
    instructions.add(new B("L" + (condBranchNb + 1), COND.EQ));
    freeReg(rd);
    return null;
  }

  public REG visitNewScope(ScopeNode scopeNode) {
    visitChildStats(scopeNode.symbolTable(), scopeNode.parentNode());
    return null;
  }

  public REG visitUnaryExpr(UnaryExpr expr) {

    if ((expr.insideExpr() instanceof IntExpr) // Set int value to negative
        && expr.operator() == UNOP.MINUS) {
      ((IntExpr) expr.insideExpr()).setNegative();
      return visit(expr.insideExpr());
    }

    REG rd = visit(expr.insideExpr());
    switch (expr.operator()) {
      case MINUS:
        String overflowErrLabel = subroutines.addOverflowErr();
        instructions.add(new RS(rd, rd, new Imm_INT(0), "BS"));
        instructions.add(new B(overflowErrLabel, true, COND.VS));
        break;
      case NEG:
        instructions.add(new EOR(rd, rd, new Imm_INT(1)));
        break;
      case LEN:
        instructions.add(new LDR(rd, new Addr(
            rd))); //load first element at this address, which is the size
        break;
      default:
        break;
    }
    return rd;
  }

  public REG visitIntExpr(IntExpr expr) {
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_INT_MEM(Integer.parseInt(expr.value())), false));
    return rd;
  }

  public REG visitBinaryExp(BinExpr binExpr) {
    REG rd = visit(binExpr.lhs());

    REG rn;
    if (rd == R10) { // push rn to avoid running out of registers
      instructions.add(new PUSH(rd));
      freeReg(rd);
      rn = visit(binExpr.rhs());
      rd = useAvailableReg();
      instructions.add(new POP(rd));
    } else {
      rn = visit(binExpr.rhs());
    }

    BinExpr.BINOP operator = binExpr.operator();
    String overflowErrLabel;
    String divByZeroLabel;

    switch (operator) {
      case OR:
        instructions.add(new ORR(rd, rd, rn));
        break;
      case AND:
        instructions.add(new AND(rd, rd, rn));
        break;
      case PLUS:
        overflowErrLabel = subroutines.addOverflowErr();
        instructions.add(new ADD(rd, rd, rn, true));
        instructions.add(new B(overflowErrLabel, true, operator.cond()));
        break;
      case MINUS:
        overflowErrLabel = subroutines.addOverflowErr();
        instructions.add(new SUB(rd, rd, rn, true));
        instructions.add(new B(overflowErrLabel, true, operator.cond()));
        break;
      case MUL:
        overflowErrLabel = subroutines.addOverflowErr();
        instructions.addAll(Arrays.asList(
            new MUL(rd, rn, rd, rn),
            new CMP(rn, rd, new Shift(ASR, 31)),
            new B(overflowErrLabel, true, operator.cond())));
        break;
      case DIV:
        divByZeroLabel = subroutines.addCheckDivideByZero();
        instructions.addAll(Arrays.asList(
            new MOV(R0, rd), new MOV(R1, rn),
            new B(divByZeroLabel, true),
            new B("__aeabi_idiv", true), new MOV(rd, R0)));
        break;
      case MOD:
        divByZeroLabel = subroutines.addCheckDivideByZero();
        instructions.addAll(Arrays.asList(
            new MOV(R0, rd), new MOV(R1, rn),
            new B(divByZeroLabel, true),
            new B("__aeabi_idivmod", true), new MOV(rd, R1)));
        break;

      case EQUAL:
      case GE:
      case GT:
      case LE:
      case LT:
      case NOTEQUAL:
        instructions.addAll(Arrays
            .asList(new CMP(rd, rn, null),
                new MOV(rd, new Imm_INT(1), operator.cond()),
                new MOV(rd, new Imm_INT(0),
                    BinExpr.BINOP.opposites().get(operator).cond())));
        break;

    }
    freeReg(rn);
    return rd;
  }

  public REG visitStringExpr(StringExpr stringExpr) {
    String field = subroutines.addStringField(stringExpr.getValue());
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_STRING_MEM(field), false));
    return rd;
  }

  public REG visitCharExpr(CharExpr charExpr) {
    REG rd = useAvailableReg();
    instructions
        .add(new MOV(rd, !(charExpr.isEscapeChar()) ? new Imm_STRING(
            "'" + charExpr.value() + "'")
            : new Imm_INT(Integer.parseInt(charExpr.value()))));
    return rd;
  }

  public REG visitBoolExpr(BoolExpr boolExpr) {
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, new Imm_INT(boolExpr.value() ? 1 : 0)));
    return rd;
  }

  public REG visitArrayLiter(ArrayLiter arrayLiter) {
    REG arrAddress = useAvailableReg();
    Expr[] array = arrayLiter.elems();
    int elemSize = 0;
    int size = array.length;

    if (size > 0) {
      elemSize = isByteSize(((ArrType) arrayLiter.type()).getArrayElem())
          ? BYTE_SIZE : WORD_SIZE;
    }

    // malloc the number of elements plus one for to hold the size
    loadArg(new Imm_INT_MEM(size * elemSize + WORD_SIZE), false);
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

  private REG saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    instructions.add(
        new STR(rd, new Addr(rn, true, new Imm_INT(offset)),
            isByteSize(varType),
            update));
    return null;
  }

  private void storeArrayElem(Expr expr, REG objectAddr, int offset) {
    REG rd = visit(expr);
    saveVarData(expr.type(), rd, objectAddr, offset, false);
    freeReg(rd);
  }

  public REG visitArrayElemLHS(ArrayElemLHS arrayElemLHS) {
    return visitArrayElem(arrayElemLHS);
  }

  public REG visitArrayElemRHS(ArrayElemRHS arrayElemRHS) {
    REG rd = visitArrayElem(arrayElemRHS);
    instructions.add(new LDR(rd, new Addr(rd),
        isByteSize(arrayElemRHS.type()))); // load value of array elem
    return rd;
  }

  // returns address of array elem
  public REG visitArrayElem(ArrayElem arrayElem) {
    REG arrAddress = loadFromStack(arrayElem.varName());
    REG index;

    for (Expr indexExpr : arrayElem.indexes()) {
      index = visit(indexExpr); // simple array case
      instructions.add(new LDR(arrAddress, new Addr(arrAddress)));
      setArgs(new REG[]{index, arrAddress});
      String checkArrayBoundsLabel = subroutines.addCheckArrayBounds();
      instructions.add(new B(checkArrayBoundsLabel, true));
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

  public REG visitPairElemLHS(PairElemLHS pairElemLHS) {
    return visitPairElem(pairElemLHS);
  }

  public REG visitPairElemRHS(PairElemRHS pairElemRHS) {
    REG rd = visitPairElem(pairElemRHS);
    instructions.add(new LDR(rd, new Addr(rd), isByteSize(pairElemRHS.type())));
    return rd; // returns value of pair element
  }

  // returns address of pair element
  public REG visitPairElem(PairElem pairElem) {
    REG rd = visit(pairElem.expr());
    moveArg(rd);
    String nullPointerLabel = subroutines.addNullPointerCheck();
    instructions.add(new B(nullPointerLabel, true));
    instructions.add(new LDR(rd,
        new Addr(rd, true, new Imm_INT(pairElem.posInPair() * WORD_SIZE)),
        false));
    return rd;
  }

  public REG visitPair(Pair pair) {
    loadArg(new Imm_INT_MEM(2 * WORD_SIZE), false);
    instructions.add(new B("malloc", true));
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, R0)); // fetch address of pair
    storeExpInHeap(pair.fst(), rd, 0);
    storeExpInHeap(pair.snd(), rd, 4);
    return rd;
  }

  public REG visitNullPair() {
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_INT_MEM(0), false));
    return rd;
  }

  private void storeExpInHeap(Expr expr, REG objectAddr, int offset) {
    REG rd = visit(expr);
    loadArg(new Imm_INT_MEM(isByteSize(expr.type()) ? 1 : 4), false);
    instructions.add(new B("malloc", true));
    saveVarData(expr.type(), rd, R0, 0, false);
    instructions
        .add(new STR(R0, new Addr(objectAddr, true, new Imm_INT(offset))));
    freeReg(rd);
  }

  public REG visitIdentLHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(ident.varName());
    instructions.add(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  public REG visitIdentRHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(ident.varName());
    instructions.add(new LDR(rd, new Addr(SP, true, new Imm_INT(offset)),
        isByteSize(ident.type())));
    return rd;
  }

  private void loadArg(Operand op2, boolean isByteSize) {
    instructions.add(new LDR(R0, op2, isByteSize));
  }

  private void moveArg(Operand op2) {
    instructions.add(new MOV(R0, op2));
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

  private REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    instructions.add(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  public REG visitReturn(ReturnNode returnNode) {
    REG rd = visit(returnNode.expr());
    instructions.add(new MOV(R0, rd));
    instructions.add(new ADD(SP, SP, new Imm_INT(totalStackOffset)));
    instructions.add(new POP(PC));
    freeReg(rd);
    return null;
  }

  public REG visitFuncCall(FuncCall funcCall) {
    visit(funcCall.argsList());
    instructions.add(new B("f_" + funcCall.funcName(), true));
    instructions.add(
        new ADD(SP, SP, new Imm_INT(funcCall.argsList().bytesPushed()), false));
    currentST.decrementStackOffset(funcCall.argsList().bytesPushed());
    REG rd = useAvailableReg();
    instructions.add(new MOV(rd, R0));
    return rd;
  }

  public REG visitParams(ListExpr listExpr) {
    List<String> paramNames = listExpr.paramNames();
    List<Expr> exprList = listExpr.exprList();
    // LR has been pushed and we have some space for the variable declaration
    // the arguments reside at this offset from the current stack pointer
    int offset = WORD_SIZE + scopeStackOffset;
    for (int i = 0; i < exprList.size(); i++) {
      currentST.lookUpVarScope(paramNames.get(i)).setLocalOffset(offset);
      offset += isByteSize(exprList.get(i).type()) ? 1 : 4;
    }
    return null;
  }

  public REG visitArgs(ListExpr listExpr) {
    List<Expr> reverseArgs = new ArrayList<>(listExpr.exprList());
    Collections.reverse(reverseArgs);
    for (Expr e : reverseArgs) {
      REG rd = visit(e);
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

  public static boolean isByteSize(Type type) {
    return type.equals(CharType.getInstance())
        || type.equals(BoolType.getInstance());
  }

}
