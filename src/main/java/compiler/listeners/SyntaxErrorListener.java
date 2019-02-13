package compiler.listeners;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
  private int nbSyntaxErrors = 0;
  StringBuilder sb;

  public SyntaxErrorListener() {
    sb = new StringBuilder();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line, int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    charPositionInLine++;

    sb.append("Syntax Error at ")
        .append(line).append(":")
        .append(charPositionInLine)
        .append(" -- ")
        .append(msg)
        .append('\n');

    nbSyntaxErrors++;
  }

  public void printCompilationStatus() {
    if (nbSyntaxErrors > 0) {
      System.err.println("Compilation failed! " + nbSyntaxErrors + " "
          + "Syntactic error" + ((nbSyntaxErrors > 1) ? "s" : ""));
      System.err.println("Exit code 100 returned");
      System.err.println(sb.toString());
      System.exit(100);
    }
  }
}
