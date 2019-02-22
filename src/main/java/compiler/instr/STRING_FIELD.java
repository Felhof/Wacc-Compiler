package compiler.instr;

public class STRING_FIELD extends Instr {

  private String string;

  public STRING_FIELD(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
      return "\t\t.word " + (string.length() - nbSpecialChar(string))
          + "\n\t\t.ascii  " + string;
  }

  public static int nbSpecialChar (String s) {
    String[] escape = {"\\0", "\\b", "\\t", "\\n", "\\f", "\\r"};
    String temp;
    temp = s.substring(1, s.length() - 1);
    for (int i = 0; i < escape.length; i++) {
      temp = temp.replace(escape[i], " ");
    }
    return (s.length() - temp.length());
  }
}
