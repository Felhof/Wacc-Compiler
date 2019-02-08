package compiler.visitors.identifiers;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(boolean value) {
    this.value = value;
    super.type = TYPE.BOOL;
  }

  public boolean value() {
    return value;
  }

  @Override
  public String toString() {
    return "BoolExpr(" + value + ")";
  }
}
