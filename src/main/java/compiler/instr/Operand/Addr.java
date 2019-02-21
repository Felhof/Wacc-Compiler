package compiler.instr.Operand;

import compiler.instr.REG;

public class Addr implements Operand {

  private REG rn;
  private boolean preIndex;
  private Operand shift;

  public Addr(REG rn) {
    this.rn = rn;
  }

  public Addr(REG rn, boolean preIndex, Operand op2) {
    this.rn = rn;
    this.preIndex = preIndex;
    this.shift = op2;
  }

  @Override
  public String toString() {
    if (shift == null || isOffsetZero()) {
      return "[" + rn.toString() + "]";
    } else if (preIndex) {
      return "[" + rn.toString() + ", " + shift.toString() + "]";
    } else {
      return "[" + rn.toString() + "]" + ", " + shift.toString();
    }
  }

  private boolean isOffsetZero() {
    return (shift instanceof Imm_INT && ((Imm_INT) shift).value == 0)
        || (shift instanceof Imm_INT_MEM && ((Imm_INT_MEM) shift).value == 0);
  }

}
