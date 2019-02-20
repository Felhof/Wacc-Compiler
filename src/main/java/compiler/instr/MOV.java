package compiler.instr;

import compiler.instr.Operand.Operand;

public class MOV extends Instr {
  private REG rd;
  private Operand op2;

  public MOV(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "\tMOV " + rd.toString() + ", " + op2.toString();
  }
}
