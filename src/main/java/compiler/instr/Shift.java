package compiler.instr;

public class Shift {
  private ShiftType type;
  private int value;

  public Shift(ShiftType type, int value) {
    this.type = type;
    this.value = value;
  }

  @Override
  public String toString() {
    return type.toString() +
        " #" + value;
  }
}
