package compiler.visitors.NodeElements;

public abstract class Expr extends AssignRHS {
  protected boolean hasBrackets = false;

  public Expr(Type type) {
    this.type = type;
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }

  public Type type() {
    return super.type();
  }
}
