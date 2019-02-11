package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.NodeElem;
import compiler.visitors.NodeElements.Types.Type;

public abstract class Expr extends NodeElem {
  protected boolean hasBrackets = false;

  public Expr(Type type) {
    super(type);
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }
}
