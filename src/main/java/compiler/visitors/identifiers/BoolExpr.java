package compiler.visitors.identifiers;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(boolean value) {
    super(TYPE.BOOL);
    this.value = value;
  }

  public boolean value() {
    return value;
  }

  @Override
  public String toString() {
    return "BoolExpr(" + value + ")";
  }
}
