package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.Type;

public abstract class Expr extends AssignRHS {
  protected boolean hasBrackets = false;

  public Expr(Type type) {
    super(type);
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }

  public Type type() {
    return super.type();
  }
}
