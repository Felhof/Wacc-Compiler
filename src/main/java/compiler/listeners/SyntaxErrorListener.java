package compiler.listeners;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
  private int nbSyntaxErrors = 0;

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer,
      Object offendingSymbol,
      int line, int charPositionInLine,
      String msg,
      RecognitionException e)
  {
    //System.err.println("Syntactic Error during compilation, line " + line + ":" + charPositionInLine+ ":");
    System.err.println(msg);
    nbSyntaxErrors++;
  }

  public int getNbSyntaxErrors() {
    return nbSyntaxErrors;
  }

}
