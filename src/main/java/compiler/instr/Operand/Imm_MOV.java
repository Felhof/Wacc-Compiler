package compiler.instr.Operand;

public class Imm_MOV implements Operand {
  public int value;

  public Imm_MOV(String value) {
    this.value = Integer.parseInt(value);
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}