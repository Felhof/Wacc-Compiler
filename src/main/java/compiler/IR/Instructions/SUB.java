package compiler.IR.Instructions;

import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;

public class SUB extends Instr {

  private REG rd;
  private REG rn;
  private Operand op2;
  private boolean setCPSR = false;

  public SUB(REG rd, REG rn, Operand op2) {
    this.rd = rd;
    this.rn = rn;
    this.op2 = op2;
  }

  public SUB(REG rd, REG rn, Operand op2, boolean setCPSR) {
    this(rd, rn, op2);
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
