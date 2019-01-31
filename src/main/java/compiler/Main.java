package compiler;

import java.io.IOException;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import antlr.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    BasicLexer lexer = lexer();
    List<? extends Token> tokens = lexer.getAllTokens();

    for (Token tok: tokens) {
      System.out.println(tok);
    }
    //wait for tokens to print

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    ParseTree tree = parser(tokenStream);

    // AST ast = new AST(tree);
    // System.out.println(ast);

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
    return tree;
  }


}

