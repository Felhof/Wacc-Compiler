package compiler.IR.Instructions;

import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;

public class MOV extends Instr {
  private REG rd;
  private Operand op2;
  private COND cond = null;

  public MOV (REG rd, Operand op2){
    this.rd = rd;
    this.op2 = op2;
  }

  public MOV(REG rd, Operand op2, COND cond) {
    this(rd, op2);
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tMOV" + ((cond != null) ? cond.toString() : "" )
        + " " + rd.toString() + ", " + op2.toString();
  }
}
