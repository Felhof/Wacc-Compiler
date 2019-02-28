package compiler.listeners;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {
  private String type;
  private int nbOfErrors;
  private List<String> listOfErrors;

  public ErrorListener(String type) {
    this.type = type;
    this.nbOfErrors = 0;
    listOfErrors = new ArrayList<>();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line, int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    charPositionInLine++;

    StringBuilder sb = new StringBuilder();

    sb.append(type)
        .append(" Error at ")
        .append(line).append(":")
        .append(charPositionInLine)
        .append(" -- ")
        .append(msg)
        .append('\n');

    listOfErrors.add(sb.toString());
    nbOfErrors++;
  }

  public int printCompilationStatus() {
    if (nbOfErrors > 0) {
      int exitCode = type.equals("Syntax") ? 100 : 200;
      System.err.println("Compilation failed! " + nbOfErrors + " "
          + type + " error" + ((nbOfErrors > 1) ? "s" : ""));
      System.err.println("Exit code " + exitCode + " returned");
      listOfErrors.forEach(System.err::print);
      return exitCode;
    }
    return 0;
  }
}
