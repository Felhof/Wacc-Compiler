package compiler.instr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STRING_FIELD extends Instr {

  private String string;
  public static List<String> escape = new ArrayList<>(Arrays.asList("\\0", "\\b", "\\t", "\\n", "\\f", "\\r"));
  public static List<Character> escapeChars = new ArrayList<>(Arrays.asList('\0', '\b', '\t', '\n', '\f', '\r'));


  public STRING_FIELD(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
      return "\t\t.word " + (string.length() - nbSpecialChar(string))
          + "\n\t\t.ascii  " + string;
  }

  public static int nbSpecialChar (String s) {
    String temp;
    temp = s.substring(1, s.length() - 1);
    for (String anEscape : escape) {
      temp = temp.replace(anEscape, " ");
    }
    return (s.length() - temp.length());
  }
}
