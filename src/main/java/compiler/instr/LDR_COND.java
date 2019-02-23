package compiler.instr;

import compiler.instr.Operand.Operand;

public class LDR_COND extends Instr {


  REG rd;
  Operand op;
  COND cond;

  public LDR_COND(REG rd, Operand op, COND cond) {
    this.rd = rd;
    this.op = op;
    this.cond = cond;
  }


  @Override
  public String toString() {
    return "\tLDR"
            + cond.toString() + " "
            + rd.toString()
            + ", " + op.toString();
  }

  public static enum COND {
    EQ, NE, GT, LT,
    GE, LE, VS, CS
  }


}