package compiler.IR.Instructions;

import compiler.IR.Operand.REG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class POP extends Instr {

  private List<REG> regsToPop;

  public POP(List<REG> regsToPop) {
    this.regsToPop = regsToPop;
  }
  public POP(REG regToPop) {
    this.regsToPop = new ArrayList<>(Arrays.asList(regToPop));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\tPOP {");
    regsToPop.forEach(r -> sb.append(r.toString()).append(", "));
    sb.delete(sb.length() - 2, sb.length());
    sb.append("}");
    return sb.toString();
  }
}
