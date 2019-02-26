package compiler.instr;

import compiler.instr.Operand.Operand;

public class LDR extends Instr {

  private REG rd;
  private Operand op2;
  private boolean isByteInstr = false;
  private COND cond;

  public LDR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  public LDR(REG rd, Operand op2, boolean isByteInstr) {
    this(rd, op2);
    this.isByteInstr = isByteInstr;
  }

  public LDR(REG rd, Operand op2, COND cond) {
    this(rd, op2);
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tLDR" + (isByteInstr ? "SB" : "") + ((cond != null) ? cond.toString() + " " : " ")
      + rd.toString() + ", " + op2.toString();
  }

  public enum COND {
    EQ, NE, GT, LT,
    GE, LE, VS, CS


  }
}

