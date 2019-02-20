package compiler.instr;

import java.util.Arrays;

public class STRING_FIELD extends Instr {

  String string;

  public STRING_FIELD(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
      return "\t\t.word " + (string.length() - 2 - nbSpecialChar())
          + "\n\t\t.ascii  " + string;
  }

  private int nbSpecialChar () {
    String[] escape = {"\0", "\b", "\t", "\n", "\f", "\r", "\"", "\\"};
    String temp = string;
    Arrays.stream(escape).forEach(c -> temp.replace(c, ""));
    return (string.length() - temp.length()) / 2;
  }
}
