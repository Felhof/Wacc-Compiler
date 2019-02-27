package compiler.IR.Instructions;

import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;

public class STR implements Instr {

  private REG rd;
  private Operand op2;
  private boolean isByteInstr = false;
  private boolean updateRn = false;

  public STR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  public STR(REG rd, Operand op2, boolean isByteInstr, boolean updateRn) {
    this(rd, op2);
    this.isByteInstr = isByteInstr;
    this.updateRn = updateRn;
  }

  public STR(REG rd, Operand op2, boolean isByteInstr) {
    this(rd, op2);
    this.isByteInstr = isByteInstr;
  }



  @Override
  public String toString() {
    return "\tSTR" + (isByteInstr ? "B " : " ")
        + rd.toString() + ", "
        + op2.toString() + (
        updateRn ? "!" : "");
  }
}