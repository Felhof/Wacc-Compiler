package compiler.visitors.identifiers;

public class IntExpr extends Expr {
  private int value;

  public IntExpr(String value) {
    super(TYPE.INT);
    this.value = Integer.parseInt(value);
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "IntExpr(" + value + ')';
  }
}
