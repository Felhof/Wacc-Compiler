package compiler.visitors.backend;

import static compiler.IR.Operand.REG.*;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.Type;
import compiler.IR.IR;
import compiler.IR.Instructions.*;
import compiler.IR.Operand.*;
import compiler.IR.Subroutines;
import java.util.List;

// Abstract class that holds all the shared fields and methods useful for
// code generator classes such as the AST visitors
public abstract class CodeGenerator {

  // Shared references among children
  protected IR program;
  protected Subroutines subroutines;
  protected List<REG> availableRegs;

  protected static SymbolTable currentST;
  protected static int scopeStackOffset;

  public CodeGenerator(IR program, Subroutines subroutines, List<REG> availableRegs) {
    this.program = program;
    this.subroutines = subroutines;
    this.availableRegs = availableRegs;
  }

  // Utils to use efficiently the registers

  protected REG useAvailableReg() {
    return availableRegs.remove(0);
  }

  protected void freeReg(REG reg) {
    if (allUsableRegs.contains(reg)) {
      availableRegs.add(0, reg);
    }
  }

  // Utils to save and load data from stack

  protected void saveVarData(Type varType, REG rd, REG rn, int offset,
      boolean update) {
    program.addInstr(
        new STR(rd, new Addr(rn, true, new Imm_INT(offset)),
            varType.isByteSize(), update));
  }

  protected REG loadFromStack(String varName) {
    REG rd = useAvailableReg();
    int offset = currentST.getTotalOffset(varName);
    program.addInstr(new ADD(rd, SP, new Imm_INT(offset)));
    return rd;
  }

  // Utils to set arguments into the appropriate registers before
  // branch to a function label

  protected void loadArg(Operand op2, boolean isByteSize) {
    program.addInstr(new LDR(R0, op2, isByteSize));
  }

  protected void setArgs(Operand[] ops) {
    for (int i = 0; i < ops.length && i < NBR_ARG_REGS; i++) {
      program.addInstr(new MOV(REG.values()[i], ops[i]));
    }
  }

  protected void moveArg(Operand op2) {
    program.addInstr(new MOV(R0, op2));
  }

}
