package compiler.instr.Operand;

public class Imm_LDR implements Operand {
  public int value;

  public Imm_LDR(String value) { // WHY IMM_LDR ?
    this.value = Integer.parseInt(value);
  }

  @Override
  public String toString() {
    return "=" + value;
  }
}
