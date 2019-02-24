package compiler.instr;

import compiler.instr.Operand.Operand;

public class ADD extends Instr {

  private REG rd;
  private REG rn;
  private Operand op;
  private boolean setCPSR = false;

  public ADD(REG rd, REG rn, Operand op) {
    this.rd = rd;
    this.rn = rn;
    this.op = op;
  }

  public ADD(REG rd, REG rn, Operand op, boolean setCPSR) {
    this(rd, rn, op);
    this.setCPSR = setCPSR;
  }

  @Override
  public String toString() {
    return "\tADD" + ((setCPSR) ? "S " : " ")
            + rd.toString()
            +", "+ rn.toString()
            +", " + op.toString();
  }

}
