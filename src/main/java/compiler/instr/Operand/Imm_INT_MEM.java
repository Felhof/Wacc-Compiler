package compiler.instr.Operand;

public class Imm_INT_MEM implements Operand {
  public int value;

  public Imm_INT_MEM(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }
}
