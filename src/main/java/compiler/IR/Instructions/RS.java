package compiler.IR.Instructions;

import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;

public class RS implements Instr {

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
