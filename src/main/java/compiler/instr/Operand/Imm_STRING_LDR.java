package compiler.instr.Operand;

public class Imm_STRING_LDR implements Operand {
  public String value;

  public Imm_STRING_LDR(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }

}
