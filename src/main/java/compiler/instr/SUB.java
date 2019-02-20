package compiler.instr;

import compiler.instr.Operand.Operand;

public class SUB extends Instr {

  REG rd1;
  REG rd2;
  Operand op;

  public SUB(REG rd1, REG rd2, Operand op) {
    this.rd1 = rd1;
    this.rd2 = rd2;
    this.op = op;
  }


  @Override
  public String toString() {
    return "\tSUB "
            + rd1.toString()
            +", "+ rd2.toString()
            +", " + op.toString();
  }

}
