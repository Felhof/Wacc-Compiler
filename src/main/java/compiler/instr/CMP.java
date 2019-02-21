package compiler.instr;

import compiler.instr.Operand.Operand;

public class CMP extends Instr {


  REG rd;
  Operand op;

  public CMP(REG rd, Operand op) {
    this.rd = rd;
    this.op = op;
  }


  @Override
  public String toString() {
    return "\tCMP "
            + rd.toString()
            +", "+ op.toString();
  }


}