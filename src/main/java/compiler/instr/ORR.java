package compiler.instr;

import compiler.instr.Operand.Operand;

public class ORR extends Instr {

  private REG rd;
  private Operand op1;
  private Operand op2;

  public ORR(REG rd, Operand op1, Operand op2) {
    this.rd = rd;
    this.op1 = op1;
    this.op2 = op2;
  }


  @Override
  public String toString() {
    return "\tORR "
            + rd.toString()
            +", "+ op1.toString()
            +", " + op2.toString();
  }

}