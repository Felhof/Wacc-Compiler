package compiler.instr;

import compiler.instr.LDR_COND.COND;
import compiler.instr.Operand.Operand;

public class MOV extends Instr {
  private REG rd;
  private Operand op2;
  private COND cond;

  public MOV(REG rd, Operand op2, COND cond) {
    this.rd = rd;
    this.op2 = op2;
    this.cond = cond;
  }

  public MOV (REG rd, Operand op2){
    this(rd, op2, null);
  }

  @Override
  public String toString() {
    return "\tMOV" + ((cond != null) ? cond.toString() : "" )
        + " " + rd.toString() + ", " + op2.toString();
  }
}
