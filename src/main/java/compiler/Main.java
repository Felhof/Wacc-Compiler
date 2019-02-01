package compiler;

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import antlr.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    BasicLexer lexer = lexer();
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    ParseTree tree = parser(tokenStream);
  }

  public static BasicLexer lexer() {
    CharStream input = null;
    try {
      input = CharStreams.fromFileName("hello.wacc");
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BasicLexer(input);
  }

  public static ParseTree parser(CommonTokenStream stream) {
    BasicParser parser = new BasicParser(stream);

    ParseTree tree = parser.prog();
    System.out.println(tree.toStringTree(parser));
    return tree;
  }


}

