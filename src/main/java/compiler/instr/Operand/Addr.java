package compiler.instr.Operand;

import compiler.instr.REG;

public class Addr implements Operand {
  public REG rd;

  public Addr(REG rd) {
    this.rd = rd;
  }

  @Override
  public String toString() {
    return "[" + rd.toString() + "]";
  }

}
