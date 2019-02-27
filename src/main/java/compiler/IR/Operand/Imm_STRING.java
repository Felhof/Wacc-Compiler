package compiler.IR.Operand;

public class Imm_STRING implements Operand {
  public String value;

  public Imm_STRING(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}
