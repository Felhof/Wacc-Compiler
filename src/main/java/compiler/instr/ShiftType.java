package compiler.instr;

public enum ShiftType {
  ASR("ASR"), LSR("LSR");

  private String s;
  ShiftType(String s) {
    this.s = s;
  }

  @Override
  public String toString() {
    return s;
  }
}
