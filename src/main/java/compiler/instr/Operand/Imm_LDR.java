package compiler.instr.Operand;

public class Imm_LDR implements Operand {
  public String value;

  public Imm_LDR(String value) { // WHY IMM_LDR ?
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }
}
