package compiler.IR.Operand;

public class Shift {
  private SHIFT_TYPE type;
  private int value;

  public Shift(SHIFT_TYPE type, int value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public String toString() {
    return type.toString() +
        " #" + value;
  }

  public static enum SHIFT_TYPE {
    ASR, LSL, LSR;
  }
}
