package compiler.instr;

public class MUL extends Instr {

  private REG rdLo;
  private REG rnLi;
  private REG rm;
  private REG rs;

  public MUL(REG rdLo, REG rnLi, REG rm, REG rs) {
    this.rdLo = rdLo;
    this.rnLi = rnLi;
    this.rm = rm;
    this.rs = rs;
  }

  @Override
  public String toString() {
    return "\tSMULL " + rdLo.toString() + ", " + rnLi.toString() + ", " + rm.toString() + ", " + rs.toString();
  }
}
