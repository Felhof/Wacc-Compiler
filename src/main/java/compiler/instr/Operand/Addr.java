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
    if (shift == null) {
      return "[" + rn.toString() + "]";
    } else if (preIndex) {
      return "[" + rn.toString() + ", " + shift.toString() + "]";
    } else {
      return "[" + rn.toString() + "]" + ", " + shift.toString();
    }
  }

}
