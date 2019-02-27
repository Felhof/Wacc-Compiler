package compiler.IR.Instructions;

import compiler.IR.Operand.Operand;
import compiler.IR.Operand.REG;
import compiler.IR.Operand.Shift;

public class CMP implements Instr {

  private REG rd;
  private Operand op;
  private Shift s;

  public CMP(REG rd, Operand op) {
    this.rd = rd;
    this.op = op;
  }

  public CMP(REG rd, Operand op, Shift s) {
    this(rd, op);
    this.s = s;
  }


  @Override
  public String toString() {
    return "\tCMP "
            + rd.toString()
            +", "+ op.toString() + ((s != null) ? (", " + s.toString()) : "");
  }

}