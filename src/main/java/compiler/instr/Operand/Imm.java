package compiler.instr.Operand;

public class Imm implements Operand {
  public int value;

  public Imm(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}