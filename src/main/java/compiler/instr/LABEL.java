package compiler.instr;

public class LABEL extends Instr {
  private String label;

  public LABEL(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label + ":";
  }
}
