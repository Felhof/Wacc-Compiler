package compiler.instr;

import java.util.List;

public class PUSH extends Instr {

  List<Register> regsToPush;

  public PUSH(List<Register> regsToPush) {
    this.regsToPush = regsToPush;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PUSH {");
    regsToPush.forEach(r -> sb.append(r.toString()).append(", "));
    sb.deleteCharAt(sb.length() - 1);
    sb.deleteCharAt(sb.length() - 1);
    sb.append("}");
    return sb.toString();
  }
}
