package compiler;

import java.io.IOException;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import antlr.*;

public class Main {

  public static void main(String[] args) {
  }

  public static List<? extends Token> lexer(String filename) {
    CharStream input = null;
    try {
      input = CharStreams.fromFileName(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }

    BasicLexer lexer = new BasicLexer(input);
    return lexer.getAllTokens();
  }

}

