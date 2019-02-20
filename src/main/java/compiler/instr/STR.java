package compiler.instr;

import compiler.instr.Operand.Operand;

public class STR extends Instr {
  REG rd;
  Operand op2;

  public STR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "\tSTR " + rd.toString() + ", " + op2.toString();
  }
}