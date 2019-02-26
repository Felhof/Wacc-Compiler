package compiler.instr.Operand;

import compiler.visitors.CodeGenData;

public class Imm_INT implements Operand, CodeGenData {
  public int value;

  public Imm_INT(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "#" + value;
  }
}