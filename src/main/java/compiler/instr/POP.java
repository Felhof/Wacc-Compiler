package compiler.instr;

import java.util.List;

public class POP extends Instr {

  List<Register> regsToPop;

  public POP(List<Register> regsToPop) {
    this.regsToPop = regsToPop;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("POP {");
    regsToPop.forEach(r -> sb.append(r.toString()));
    sb.append("}");
    return sb.toString();
  }
}
