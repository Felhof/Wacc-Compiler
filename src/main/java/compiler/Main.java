package compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import antlr.*;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    compileProg("test.wacc");
  }

  public static BasicLexer lexFile(String filename) {
    CharStream input = null;
    try {
      input = CharStreams.fromFileName(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new BasicLexer(input);
  }

//  public static List<? extends Token> lexer(String filename) {
//    CharStream input = null;
//    try {
//      input = CharStreams.fromFileName(filename);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return new BasicLexer(input).getAllTokens();
//  }

  public static ParseTree parser(CommonTokenStream stream) {
    BasicParser parser = new BasicParser(stream);
    parser.removeErrorListeners();
    parser.addErrorListener(new VerboseListener());
    ParseTree tree = parser.prog();
    // System.out.println(tree.toStringTree(parser));
    return tree;
  }

  public static String compileProg(String filename) {
    BasicLexer lexer = lexFile(filename);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    ParseTree tree = parser(tokenStream);
    return null;
  }

  public static class VerboseListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line, int charPositionInLine,
        String msg,
        RecognitionException e)
    {
      List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
      Collections.reverse(stack);
      System.err.println("rule stack: "+stack);
      System.err.println("line "+line+":"+charPositionInLine+" at "+
          offendingSymbol+": "+msg);
    }
  }

}

