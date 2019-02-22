package compiler.instr;

public class BL extends Instr {

  private String label;
  private String cond;

  public BL(String label, String cond) {
    this.label = label;
    this.cond = cond;
  }

  @Override
  public String toString() {
    return "\tBL"+ cond + " " + label;
  }

}
