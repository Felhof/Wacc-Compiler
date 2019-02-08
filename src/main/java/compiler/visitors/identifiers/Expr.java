package compiler.visitors.identifiers;

import compiler.visitors.Returnable;

public abstract class Expr implements Identifier, Returnable {
  protected TYPE type;
  protected boolean hasBrackets = false;

  public void putBrackets() {
    this.hasBrackets = true;
  }

  public TYPE type() {
    return type;
  }
}
