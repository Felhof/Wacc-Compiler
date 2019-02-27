package compiler.visitors.backend;

import static compiler.IR.Operand.REG.R0;
import static compiler.IR.Operand.REG.SP;
import static compiler.IR.Operand.REG.allUsableRegs;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.Type;
import compiler.IR.Instructions.ADD;
import compiler.IR.Instructions.Instr;
import compiler.IR.Instructions.LDR;
import compiler.IR.Instructions.MOV;
import compiler.IR.Instructions.STR;
import compiler.IR.Operand.Addr;
import compiler.IR.Operand.Imm_INT;
import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;
import compiler.IR.SubRoutines;
import java.util.List;

public abstract class CodegenVisitor {

  public static final int WORD_SIZE = 4;
  public static final int SHIFT_TIMES_4 = 2;
  public static final int BYTE_SIZE = 1;

  protected static List<Instr> instructions; // list of ARM instructions
  // handles adding subroutines and its data fields
  protected static SubRoutines subroutines;

  protected static List<REG> availableRegs;
  protected static SymbolTable currentST;
  protected static int totalStackOffset;
  protected static int scopeStackOffset;
  protected static int nextPosInStack;
  protected static int branchNb = 0;


  /* Util methods for code generator visitors */

  protected REG useAvailableReg() {
    return availableRegs.remove(0);
  }

  protected void freeReg(REG reg) {
    if (allUsableRegs.contains(reg)) {
      availableRegs.add(0, reg);
    }
  }

  protected void loadArg(Operand op2, boolean isByteSize) {
    instructions.add(new LDR(R0, op2, isByteSize));
  }

  protected void moveArg(Operand op2) {
    instructions.add(new MOV(R0, op2));
  }

  protected void setArgs(Operand[] ops) {
    for (int i = 0; i < ops.length && i < 4; i++) {
      instructions.add(new MOV(REG.values()[i], ops[i]));
    }
  }

  protected REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    instructions.add(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  protected REG saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    instructions.add(
        new STR(rd, new Addr(rn, true, new Imm_INT(offset)),
            isByteSize(varType),
            update));
    return null;
  }

  public static boolean isByteSize(Type type) {
    return type.equals(CharType.getInstance())
        || type.equals(BoolType.getInstance());
  }

}
