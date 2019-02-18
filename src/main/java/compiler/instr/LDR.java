package compiler.instr;

public class LDR extends Instr {

  private Register rd;
  private Operand op2;

  public LDR(Register rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "LDR " + rd.toString()+ ", " + op2.toString();
  }
}
