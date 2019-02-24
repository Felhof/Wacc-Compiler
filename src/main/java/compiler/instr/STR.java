package compiler.instr;

import compiler.instr.Operand.Operand;

public class STR extends Instr {

  private REG rd;
  private Operand op2;
  private boolean isByteInstr = false;
  private boolean updateRn = false;

  public STR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  public STR(REG rd, Operand op2, boolean isByteInstr, boolean updateRn) {
    this(rd, op2);
    this.isByteInstr = isByteInstr;
    this.updateRn = updateRn;
  }

  @Override
  public String toString() {
    String strByte = "";
    if (isByteInstr) {
      strByte += "B";
    }
    return "\tSTR" + strByte + " " + rd.toString() + ", " + op2.toString() + (
        updateRn ? "!" : "");
  }
}