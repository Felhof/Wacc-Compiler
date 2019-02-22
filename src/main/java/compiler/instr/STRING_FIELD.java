package compiler.instr;

import java.util.Arrays;

public class STRING_FIELD extends Instr {

  private String string;

  public STRING_FIELD(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
      return "\t\t.word " + (string.length() - 2 - nbSpecialChar(string))
          + "\n\t\t.ascii  " + string;
  }

  public static int nbSpecialChar (String s) {
    String[] escape = {"\0", "\b", "\t", "\n", "\f", "\r", "\"", "\\"};
    String temp = s.substring(1, s.length());
    Arrays.stream(escape).forEach(c -> temp.replace(c, ""));
    return (s.length() - temp.length()) / 2;
  }
}
