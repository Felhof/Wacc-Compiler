package compiler.instr;

import compiler.instr.Operand.Operand;

public class SUB extends Instr {

  private REG rd;
  private REG rn;
  private Operand op2;
  private boolean setCPSR;

  public SUB(REG rd, REG rn, Operand op2, boolean setCPSR) {
    this.rd = rd;
    this.rn = rn;
    this.op2 = op2;
    this.setCPSR = setCPSR;
  }


  @Override
  public String toString() {
    return "\tSUB" +((setCPSR) ? "S " : " ")
            + rd.toString()
            +", "+ rn.toString()
            +", " + op2.toString();
  }

}
