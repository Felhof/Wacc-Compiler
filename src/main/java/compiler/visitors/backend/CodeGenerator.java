package compiler.visitors.backend;

import static compiler.IR.Operand.REG.*;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.Type;
import compiler.IR.IR;
import compiler.IR.Instructions.*;
import compiler.IR.Operand.*;
import compiler.IR.Subroutines;
import java.util.List;

public abstract class CodeGenerator {

  public static final int SHIFT_TIMES_4 = 2;

  protected static IR program;
  protected static Subroutines subroutines;

  protected static List<REG> availableRegs;
  protected static SymbolTable currentST;
  protected static int scopeStackOffset;

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
    program.addInstr(new LDR(R0, op2, isByteSize));
  }

  protected void moveArg(Operand op2) {
    program.addInstr(new MOV(R0, op2));
  }

  protected void setArgs(Operand[] ops) {
    for (int i = 0; i < ops.length && i < NBR_ARG_REGS; i++) {
      program.addInstr(new MOV(REG.values()[i], ops[i]));
    }
  }

  protected REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    program.addInstr(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  protected REG saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    program.addInstr(
        new STR(rd, new Addr(rn, true, new Imm_INT(offset)),
            varType.isByteSize(), update));
    return null;
  }

}
