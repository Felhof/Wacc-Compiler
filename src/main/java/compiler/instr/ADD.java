package compiler.instr;

import compiler.instr.Operand.Operand;

public class ADD extends Instr {

  REG rd;
  REG rn;
  Operand op;

  public ADD(REG rd1, REG rd2, Operand op) {
    this.rd = rd1;
    this.rn = rd2;
    this.op = op;
  }


  @Override
  public String toString() {
    return "\tADD "
            + rd.toString()
            +", "+ rn.toString()
            +", " + op.toString();
  }

}
