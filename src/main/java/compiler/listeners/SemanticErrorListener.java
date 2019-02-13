package compiler.listeners;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SemanticErrorListener extends BaseErrorListener {
  private int nbSemanticErrors = 0;
  StringBuilder sb;

  public SemanticErrorListener() {
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

    sb.append("Semantic Error at ")
        .append(line).append(":")
        .append(charPositionInLine)
        .append(" -- ")
        .append(msg)
        .append('\n');

    nbSemanticErrors++;
  }

  public void printCompilationStatus() {
    if (nbSemanticErrors > 0) {
      System.err.println("Compilation failed! " + nbSemanticErrors + " "
          + "Semantic error" + ((nbSemanticErrors > 1) ? "s" : ""));
      System.err.println("Exit code 200 returned");
      System.err.println(sb.toString());
      System.exit(200);
    }
  }
}
