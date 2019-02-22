package compiler.instr;

import compiler.instr.Operand.Operand;

public class LDR extends Instr {

  private REG rd;
  private Operand op2;
  private boolean isByteInstr;

  public LDR(REG rd, Operand op2, boolean isByteInstr) {
    this.rd = rd;
    this.op2 = op2;
    this.isByteInstr = isByteInstr;
  }

  @Override
  public String toString() {
    return "\tLDR" + (isByteInstr ? "SB " : " ") + rd.toString()+ ", " + op2.toString();
  }
}
