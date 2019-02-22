package compiler.instr;

public class BL extends Instr {

  private String label;
  private boolean checkRuntimeErr;

  public BL(String label, boolean checkRuntimeErr) {
    this.label = label;
    this.checkRuntimeErr = checkRuntimeErr;
  }

  @Override
  public String toString() {
    return "\tBL"+(checkRuntimeErr ? "VS " : " ") + label;
  }
}
