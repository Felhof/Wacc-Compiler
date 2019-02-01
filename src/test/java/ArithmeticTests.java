import compiler.Main;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertTrue;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

interface Arithmetic {}

@Category(Arithmetic.class)
public class ArithmeticTests {

  final String examplesPath = "src/test/examples/arithmetic/";

  final String number = "'[0-9]+',<[0-9]+>";
  final String plus = "'\\+',<[0-9]+>";
  final String minus = "'-',<[0-9]+>";
  final String open = "'\\(',<[0-9]+>";
  final String close = "'\\)',<[0-9]+>";
  final String eof = "'<EOF>',<EOF>";



  @Test
  public void SimpleAdditionTest(){

    String[] expectedTokens = new String[] {number , plus, number, eof};

    List<? extends Token> tokens = Main.lexer(examplesPath + "simpleAddition.wacc").getAllTokens();

    for (int i = 0; i < tokens.size(); i++){
      Pattern pattern = CreatePattern(expectedTokens[i]);

      String actualToken = tokens.get(i).toString();
      Matcher matcher = pattern.matcher(actualToken);

      assertTrue(matcher.matches());
    }
  }

  @Test
  public void SimpleSubtractionTest(){

    String[] expectedTokens = new String[] {number, minus, number, eof};

    List<? extends Token> tokens = Main.lexer(examplesPath + "simpleSubtraction.wacc").getAllTokens();

    System.out.println("Tokens: " + tokens.toString());

    for (int i = 0; i < tokens.size(); i++){
      Pattern pattern = CreatePattern(expectedTokens[i]);

      String actualToken = tokens.get(i).toString();
      Matcher matcher = pattern.matcher(actualToken);

      System.out.println("Expected Token: " + expectedTokens[i] + "\n");
      System.out.println("Actual Token: " + actualToken + "\n");

      assertTrue(matcher.matches());
    }
  }

  @Test
  public void BracketTest(){
    String[] expectedTokens = new String[] {open, number , plus, number, close, plus, number, eof};

    List<? extends Token> tokens = Main.lexer(examplesPath + "brackets.wacc").getAllTokens();

    System.out.println("Tokens: " + tokens.toString());

    for (int i = 0; i < tokens.size(); i++){
      Pattern pattern = CreatePattern(expectedTokens[i]);

      String actualToken = tokens.get(i).toString();
      Matcher matcher = pattern.matcher(actualToken);

      assertTrue(matcher.matches());
    }

  }

  private Pattern CreatePattern (String content) {
    String patternStr = "\\[@-?[0-9]+,[0-9]+:[0-9]+="
            + content
            + ",[0-9]+:[0-9]+\\]";

    return Pattern.compile(patternStr);
  }

}
