package compiler;

import compiler.visitors.ReturnFunctionVisitor;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import antlr.*;
import compiler.visitors.SyntaxVisitor;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {

    //int nbSyntaxErrors = compileProg(args[0]);
    int nbSyntaxErrors = compileProg("src/test/invalid/syntaxErr/function/functionConditionalNoReturn.wacc");
    if (nbSyntaxErrors > 0) {
      System.err.println(nbSyntaxErrors +" syntax error(s)");
      System.out.println("Exit code 100 returned");
      System.exit(100);
    }
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

  public static int parser(CommonTokenStream stream) {
    BasicParser parser = new BasicParser(stream);

    parser.removeErrorListeners();
    VerboseListener errorListener = new VerboseListener();
    parser.addErrorListener(errorListener);

    ParseTree tree = parser.prog();
    int nbSyntaxErrors = errorListener.getNbSyntaxErrors();
    // System.out.println(tree.toStringTree(parser));

    // Syntactic check if all functions have a return statement according to
    // the wacc rules
    ReturnFunctionVisitor returnFunctionVisitor = new ReturnFunctionVisitor(parser);
    returnFunctionVisitor.visit(tree);

    SyntaxVisitor syntaxVisitor = new SyntaxVisitor(parser);
    syntaxVisitor.visit(tree);

    return nbSyntaxErrors;
  }

  public static int compileProg(String filename) {
    BasicLexer lexer = lexFile(filename);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    return parser(tokenStream);
  }

  public static class VerboseListener extends BaseErrorListener {

    int nbSyntaxErrors = 0;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line, int charPositionInLine,
        String msg,
        RecognitionException e)
    {
      List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
      Collections.reverse(stack);
      System.err.println("Errors detected during compilation! Exit code 100 returned.\n" +
              "Syntactic Error at " + line + ":" + charPositionInLine+ " -- " + msg);
      //.err.println(msg);
      nbSyntaxErrors++;
    }

    public int getNbSyntaxErrors() {
      return nbSyntaxErrors;
    }
  }

}

