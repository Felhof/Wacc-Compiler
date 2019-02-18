package compiler.instr;

public class LDR extends Instr {

  private REG rd;
  private Operand op2;

  public LDR(REG rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "\tLDR " + rd.toString()+ ", " + op2.toString();
  }
}
