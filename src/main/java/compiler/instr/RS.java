package compiler.instr;

import compiler.instr.Operand.Operand;

public class RS extends Instr {

  private REG rd;
  private REG rn;
  private Operand op2;
  private String cond;

  public RS(REG rd, REG rn, Operand op2, String cond) {
    this.rd = rd;
    this.rn = rn;
    this.op2 = op2;
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tRS" + cond + " " + rd.toString() + ", " + rn.toString() + ", "
        + op2.toString();
  }
}
