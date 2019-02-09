package compiler.listeners;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SemanticErrorListener extends BaseErrorListener {
  private int nbSemanticErrors = 0;

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line, int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    System.err.println(msg);
    nbSemanticErrors++;
  }

  public int getNbSemanticErrors() {
    return nbSemanticErrors;
  }
}
