package compiler.instr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PUSH extends Instr {

  private List<REG> regsToPush;

  public PUSH(List<REG> regsToPush) {
    this.regsToPush = regsToPush;
  }
  public PUSH(REG regToPush) {
    this.regsToPush = new ArrayList<>(Arrays.asList(regToPush));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\tPUSH {");
    regsToPush.forEach(r -> sb.append(r.toString()).append(", "));
    sb.delete(sb.length() - 2, sb.length());
    sb.append("}");
    return sb.toString();
  }
}
