package compiler.visitors.backend;

import static compiler.AST.Types.Type.WORD_SIZE;
import static compiler.IR.Operand.REG.R0;
import static compiler.IR.Operand.REG.R1;
import static compiler.IR.Operand.REG.R10;
import static compiler.IR.Operand.REG.SP;
import static compiler.IR.Operand.Shift.SHIFT_TYPE.ASR;
import static compiler.IR.Operand.Shift.SHIFT_TYPE.LSL;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.LHS.ArrayElemLHS;
import compiler.AST.NodeElements.LHS.PairElemLHS;
import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.NodeElem;
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
import compiler.AST.Types.ArrType;
import compiler.IR.Instructions.ADD;
import compiler.IR.Instructions.AND;
import compiler.IR.Instructions.B;
import compiler.IR.Instructions.CMP;
import compiler.IR.Instructions.EOR;
import compiler.IR.Instructions.LDR;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Instructions.MOV;
import compiler.IR.Instructions.MUL;
import compiler.IR.Instructions.ORR;
import compiler.IR.Instructions.POP;
import compiler.IR.Instructions.PUSH;
import compiler.IR.Instructions.RS;
import compiler.IR.Instructions.STR;
import compiler.IR.Instructions.SUB;
import compiler.IR.Operand.Addr;
import compiler.IR.Operand.Imm_INT;
import compiler.IR.Operand.Imm_INT_MEM;
import compiler.IR.Operand.Imm_STRING;
import compiler.IR.Operand.Imm_STRING_MEM;
import compiler.IR.Operand.REG;
import compiler.IR.Operand.Reg_Shift;
import compiler.IR.Operand.Shift;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NodeElemVisitor extends CodegenVisitor {
  /* Code generator visitor to visit AST Node Elements */


  public REG visit(NodeElem data) {
    return data.accept(this);
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
      elemSize = ((ArrType) arrayLiter.type()).getElemType().getSize();
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
        arrayElemRHS.type().isByteSize())); // load value of array elem
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

      if (arrayElem.type().isByteSize()) {
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
    instructions.add(new LDR(rd, new Addr(rd), pairElemRHS.type().isByteSize()));
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
    storeExpInHeap(pair.snd(), rd, WORD_SIZE);
    return rd;
  }

  public REG visitNullPair() {
    REG rd = useAvailableReg();
    instructions.add(new LDR(rd, new Imm_INT_MEM(0), false));
    return rd;
  }

  private void storeExpInHeap(Expr expr, REG objectAddr, int offset) {
    REG rd = visit(expr);
    loadArg(new Imm_INT_MEM(expr.type().getSize()), false);
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
        ident.type().isByteSize()));
    return rd;
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
      offset += exprList.get(i).type().getSize();
    }
    return null;
  }

  public REG visitArgs(ListExpr listExpr) {
    List<Expr> reverseArgs = new ArrayList<>(listExpr.exprList());
    Collections.reverse(reverseArgs);
    for (Expr e : reverseArgs) {
      REG rd = visit(e);
      int offsetFromBase = - e.type().getSize();
      currentST.incrementStackOffset(-offsetFromBase);
      saveVarData(e.type(), rd, SP, offsetFromBase,
          true);
      freeReg(rd);
    }
    return null;
  }

}
