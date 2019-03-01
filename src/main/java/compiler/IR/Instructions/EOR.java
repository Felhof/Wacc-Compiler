package compiler.IR.Instructions;

import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;

public class EOR extends Instr{

  private REG rd;
  private REG rn;
  private Operand op2;

  public EOR(REG rd, REG rn, Operand op2) {
    this.rd = rd;
    this.rn = rn;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "\tEOR " + rd.toString() + ", " + rn.toString() + ", " + op2.toString();
  }
}
