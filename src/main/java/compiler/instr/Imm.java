package compiler.instr;

public class Imm implements Operand {
  public int value;

  public Imm(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "=" + value;
  }

}
