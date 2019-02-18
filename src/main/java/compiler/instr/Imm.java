package compiler.instr;

public class Imm implements Operand {
  public String value;

  public Imm(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }
}
