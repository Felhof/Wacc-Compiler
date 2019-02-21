package compiler.instr;

import compiler.instr.Operand.Operand;

public class LDR extends Instr {

  private REG rd;
  private Operand op2;
  private boolean isByteInstr;

  public LDR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
    this.isByteInstr = false;
  }


  public LDR(REG rd, Operand op2, boolean isByetInstr) {
    this.rd = rd;
    this.op2 = op2;
    this.isByteInstr = isByetInstr;
  }

  @Override
  public String toString() {
    String strByte = "";
    if(isByteInstr)
      strByte += "B";
    return "\tLDR" + strByte + " " + rd.toString()+ ", " + op2.toString();
  }
}
