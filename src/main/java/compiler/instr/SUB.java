package compiler.instr;

import compiler.instr.Operand.Operand;

public class SUB extends Instr {

  private REG rd;
  private REG rn;
  private Operand op2;

  public SUB(REG rd1, REG rd2, Operand op2) {
    this.rd = rd1;
    this.rn = rd2;
    this.op2 = op2;
  }


  @Override
  public String toString() {
    return "\tSUB "
            + rd.toString()
            +", "+ rn.toString()
            +", " + op2.toString();
  }

}
