package compiler.instr.Operand;

public class Imm_MOV implements Operand {
  public String value;

  public Imm_MOV(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}