package compiler.instr;

import compiler.instr.LDR_COND.COND;

public class BL extends Instr {

  private String label;
  private COND cond;

  public BL(String label) {
    this.label = label;
  }

  public BL(String label, COND cond) {
    this.label = label;
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tBL"+ ((cond != null) ? cond.toString() : "" ) + " " + label;
  }

}
