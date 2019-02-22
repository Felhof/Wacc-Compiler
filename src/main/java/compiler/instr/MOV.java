package compiler.instr;

import compiler.instr.Operand.Operand;

public class MOV extends Instr {
  private REG rd;
  private Operand op2;
  private String cond;

  public MOV (REG rd, Operand op2){
    this.rd = rd;
    this.op2 = op2;
    this.cond = "";
  }

  public MOV(REG rd, Operand op2, String cond) {
    this.rd = rd;
    this.op2 = op2;
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tMOV" + cond + " " + rd.toString() + ", " + op2.toString();
  }
}
