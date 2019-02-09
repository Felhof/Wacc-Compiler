package compiler.visitors.identifiers;

import compiler.visitors.Returnable;

public class Expr implements Identifier, Returnable {
  protected TYPE type;
  protected boolean hasBrackets = false;

  public Expr(TYPE type) {
    this.type = type;
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }

  public TYPE type() {
    return type;
  }
}
