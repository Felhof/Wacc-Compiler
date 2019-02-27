package compiler.IR.Instructions;


import compiler.IR.Instructions.LDR.COND;

public class B implements Instr {

  private String label;
  private COND cond;
  private boolean isLink = false;

  public B(String label) {
    this.label = label;
  }

  public B(String label, COND cond) {
    this(label);
    this.cond = cond;
  }

  public B(String label, boolean isLink) {
    this(label);
    this.isLink = isLink;
  }

  public B(String label, boolean isLink, COND cond) {
    this(label, cond);
    this.isLink = isLink;
  }

  @Override
  public String toString() {
    return "\tB" + ((isLink) ? "L" : "") + ((cond != null) ? cond.toString() : "" ) + " " + label;
  }

}
