package compiler.listeners;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {
  private String type;
  private int nbOfErrors;
  private StringBuilder sb;

  public ErrorListener(String type) {
    this.type = type;
    this.nbOfErrors = 0;
    this.sb = new StringBuilder();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line, int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    charPositionInLine++;

    sb.append(type)
        .append(" Error at ")
        .append(line).append(":")
        .append(charPositionInLine)
        .append(" -- ")
        .append(msg)
        .append('\n');

    nbOfErrors++;
  }

  public void printCompilationStatus() {
    if (nbOfErrors > 0) {
      int exitCode = type.equals("Syntax") ? 100 : 200;
      System.err.println("Compilation failed! " + nbOfErrors + " "
          + type + " error" + ((nbOfErrors > 1) ? "s" : ""));
      System.err.println("Exit code " + exitCode + " returned");
      System.err.println(sb.toString());
      System.exit(exitCode);
    }
  }
}
