package compiler.instr.Operand;

public class Imm_STRING_MEM implements Operand {
  public String value;

  public Imm_STRING_MEM(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }

}
