package compiler.visitors.backend;

import static compiler.AST.Types.Type.WORD_SIZE;
import static compiler.IR.Operand.REG.*;
import static compiler.IR.Operand.Shift.SHIFT_TYPE.*;
import static compiler.visitors.backend.ASTVisitor.visit;

import compiler.AST.NodeElements.*;
import compiler.AST.NodeElements.LHS.*;
import compiler.AST.NodeElements.RHS.*;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Types.ArrType;
import compiler.IR.Instructions.*;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Operand.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NodeElemVisitor extends CodeGenerator {
  /* Code generator visitor to visit AST Node Elements */

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
        program.addInstr(new RS(rd, rd, new Imm_INT(0), "BS"));
        program.addInstr(new B(overflowErrLabel, true, COND.VS));
        break;
      case NEG:
        program.addInstr(new EOR(rd, rd, new Imm_INT(1)));
        break;
      case LEN:
        program.addInstr(new LDR(rd, new Addr(
            rd))); //load first element at this address, which is the size
        break;
      default:
        break;
    }
    return rd;
  }

  public REG visitIntExpr(IntExpr expr) {
    REG rd = useAvailableReg();
    program.addInstr(new LDR(rd, new Imm_INT_MEM(Integer.parseInt(expr.value())), false));
    return rd;
  }

  public REG visitBinaryExp(BinExpr binExpr) {
    REG rd = visit(binExpr.lhs());

    REG rn;
    if (rd == R10) { // push rn to avoid running out of registers
      program.addInstr(new PUSH(rd));
      freeReg(rd);
      rn = visit(binExpr.rhs());
      rd = useAvailableReg();
      program.addInstr(new POP(rd));
    } else {
      rn = visit(binExpr.rhs());
    }

    BinExpr.BINOP operator = binExpr.operator();
    String overflowErrLabel;
    String divByZeroLabel;

    switch (operator) {
      case OR:
        program.addInstr(new ORR(rd, rd, rn));
        break;
      case AND:
        program.addInstr(new AND(rd, rd, rn));
        break;
      case PLUS:
        overflowErrLabel = subroutines.addOverflowErr();
        program.addInstr(new ADD(rd, rd, rn, true));
        program.addInstr(new B(overflowErrLabel, true, operator.cond()));
        break;
      case MINUS:
        overflowErrLabel = subroutines.addOverflowErr();
        program.addInstr(new SUB(rd, rd, rn, true));
        program.addInstr(new B(overflowErrLabel, true, operator.cond()));
        break;
      case MUL:
        overflowErrLabel = subroutines.addOverflowErr();
        program.addAllInstr(Arrays.asList(
            new MUL(rd, rn, rd, rn),
            new CMP(rn, rd, new Shift(ASR, 31)),
            new B(overflowErrLabel, true, operator.cond())));
        break;
      case DIV:
        divByZeroLabel = subroutines.addCheckDivideByZero();
        program.addAllInstr(Arrays.asList(
            new MOV(R0, rd), new MOV(R1, rn),
            new B(divByZeroLabel, true),
            new B("__aeabi_idiv", true), new MOV(rd, R0)));
        break;
      case MOD:
        divByZeroLabel = subroutines.addCheckDivideByZero();
        program.addAllInstr(Arrays.asList(
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
        program.addAllInstr(Arrays
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
    program.addInstr(new LDR(rd, new Imm_STRING_MEM(field), false));
    return rd;
  }

  public REG visitCharExpr(CharExpr charExpr) {
    REG rd = useAvailableReg();
    program.addInstr(
        new MOV(rd, !(charExpr.isEscapeChar()) ? new Imm_STRING(
            "'" + charExpr.value() + "'")
            : new Imm_INT(Integer.parseInt(charExpr.value()))));
    return rd;
  }

  public REG visitBoolExpr(BoolExpr boolExpr) {
    REG rd = useAvailableReg();
    program.addInstr(new MOV(rd, new Imm_INT(boolExpr.value() ? 1 : 0)));
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
    program.addInstr(new B("malloc", true));
    program.addInstr(new MOV(arrAddress, R0));

    // store array address elements in the heap
    for (int i = 0; i < size; i++) {
      storeArrayElem(array[i], arrAddress, WORD_SIZE + i * elemSize);
    }

    // store size of the array in the heap
    REG sizeReg = useAvailableReg();
    program.addInstr(new LDR(sizeReg, new Imm_INT_MEM(size)));
    program.addInstr(new STR(sizeReg, new Addr(arrAddress)));

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
    program.addInstr(new LDR(rd, new Addr(rd),
        arrayElemRHS.type().isByteSize())); // load value of array elem
    return rd;
  }

  // returns address of array elem
  public REG visitArrayElem(ArrayElem arrayElem) {
    REG arrAddress = loadFromStack(arrayElem.varName());
    REG index;

    for (Expr indexExpr : arrayElem.indexes()) {
      index = visit(indexExpr); // simple array case
      program.addInstr(new LDR(arrAddress, new Addr(arrAddress)));
      setArgs(new REG[]{index, arrAddress});
      String checkArrayBoundsLabel = subroutines.addCheckArrayBounds();
      program.addInstr(new B(checkArrayBoundsLabel, true));
      program.addInstr(new ADD(arrAddress, arrAddress, new Imm_INT(WORD_SIZE)));

      if (arrayElem.type().isByteSize()) {
        program.addInstr(new ADD(arrAddress, arrAddress, index));
      } else {
        program.addInstr(new ADD(arrAddress, arrAddress, new Reg_Shift(index,
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
    program.addInstr(new LDR(rd, new Addr(rd), pairElemRHS.type().isByteSize()));
    return rd; // returns value of pair element
  }

  // returns address of pair element
  public REG visitPairElem(PairElem pairElem) {
    REG rd = visit(pairElem.expr());
    moveArg(rd);
    String nullPointerLabel = subroutines.addNullPointerCheck();
    program.addInstr(new B(nullPointerLabel, true));
    program.addInstr(new LDR(rd,
        new Addr(rd, true, new Imm_INT(pairElem.posInPair() * WORD_SIZE)),
        false));
    return rd;
  }

  public REG visitPair(Pair pair) {
    loadArg(new Imm_INT_MEM(2 * WORD_SIZE), false);
    program.addInstr(new B("malloc", true));
    REG rd = useAvailableReg();
    program.addInstr(new MOV(rd, R0)); // fetch address of pair
    storeExpInHeap(pair.fst(), rd, 0);
    storeExpInHeap(pair.snd(), rd, WORD_SIZE);
    return rd;
  }

  public REG visitNullPair() {
    REG rd = useAvailableReg();
    program.addInstr(new LDR(rd, new Imm_INT_MEM(0), false));
    return rd;
  }

  private void storeExpInHeap(Expr expr, REG objectAddr, int offset) {
    REG rd = visit(expr);
    loadArg(new Imm_INT_MEM(expr.type().getSize()), false);
    program.addInstr(new B("malloc", true));
    saveVarData(expr.type(), rd, R0, 0, false);
    program.addInstr(
        new STR(R0, new Addr(objectAddr, true, new Imm_INT(offset))));
    freeReg(rd);
  }

  public REG visitIdentLHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(ident.varName());
    program.addInstr(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  public REG visitIdentRHS(Ident ident) {
    REG rd = useAvailableReg();
    int offset = CodeGenerator.currentST.getTotalOffset(ident.varName());
    program.addInstr(new LDR(rd, new Addr(SP, true, new Imm_INT(offset)),
        ident.type().isByteSize()));
    return rd;
  }

  public REG visitFuncCall(FuncCall funcCall) {
    visit(funcCall.argsList());
    program.addInstr(new B("f_" + funcCall.funcName(), true));
    program.addInstr(
        new ADD(SP, SP, new Imm_INT(funcCall.argsList().bytesPushed()), false));
    currentST.decrementStackOffset(funcCall.argsList().bytesPushed());
    REG rd = useAvailableReg();
    program.addInstr(new MOV(rd, R0));
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

  /* utils */

  // node elem
  public REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    program.addInstr(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

}
