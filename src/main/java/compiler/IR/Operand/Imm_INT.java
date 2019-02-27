package compiler.IR.Operand;

public class Imm_INT implements Operand {
  public int value;

  public Imm_INT(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}