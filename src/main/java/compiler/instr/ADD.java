package compiler.instr;

import compiler.instr.Operand.Operand;

public class ADD extends Instr {

  private REG rd;
  private REG rn;
  private Operand op;
  private boolean setCPSR;

  public ADD(REG rd1, REG rd2, Operand op, boolean setCPSR) {
    this.rd = rd1;
    this.rn = rd2;
    this.op = op;
    this.setCPSR = setCPSR;
  }

  public ADD(REG rd, REG rn, Operand op) {
    this(rd, rn, op, false);
  }

  @Override
  public String toString() {
    return "\tADD" + ((setCPSR) ? "S " : " ")
            + rd.toString()
            +", "+ rn.toString()
            +", " + op.toString();
  }

}
