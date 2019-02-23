package compiler.instr;

import compiler.instr.Operand.Operand;

public class CMP extends Instr {

  private REG rd;
  private Operand op;
  private Shift s;

  public CMP(REG rd, Operand op) {
    this.rd = rd;
    this.op = op;
  }

  public CMP(REG rd, Operand op, Shift s) {
    this.rd = rd;
    this.op = op;
    this.s = s;
  }


  @Override
  public String toString() {
    return "\tCMP "
            + rd.toString()
            +", "+ op.toString() + ((s != null) ? (", " + s.toString()) : "");
  }

}