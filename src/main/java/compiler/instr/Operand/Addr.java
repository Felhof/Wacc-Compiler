package compiler.instr.Operand;

public class Addr implements Operand {
  public String value;

  public Addr(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "[" + value + "]";
  }

}
