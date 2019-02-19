package compiler.instr;

public class BL extends Instr {

  private String label;

  public BL(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "\tBL " + label;
  }
}
