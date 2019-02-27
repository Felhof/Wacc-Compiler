package compiler.IR.Operand;

public class Reg_Shift implements Operand {
  private REG rm;
  private Shift shift;

  public Reg_Shift(REG rm, Shift shift) {
    this.rm = rm;
    this.shift = shift;
  }

  @Override
  public String toString() {
    return rm.toString() + ", " + shift.toString();
  }
}
