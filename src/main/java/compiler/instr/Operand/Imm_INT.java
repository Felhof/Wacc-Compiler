package compiler.instr.Operand;

public class Imm_INT implements Operand {
  public int value;

  public Imm_INT(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}