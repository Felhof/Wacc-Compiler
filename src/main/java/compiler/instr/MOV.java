package compiler.instr;

public class MOV extends Instr {
  Register rd;
  Operand op2;

  public MOV(Register rd, Operand op2) {
    this.rd = rd;
    this.op2 = op2;
  }

  @Override
  public String toString() {
    return "MOV " + rd.toString() + ", " + op2.toString();
  }
}
