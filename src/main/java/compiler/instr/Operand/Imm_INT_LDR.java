package compiler.instr.Operand;

public class Imm_INT_LDR implements Operand {
  public int value;

  public Imm_INT_LDR(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public String toString() {
    return "=" + value;
  }
}
