package compiler.visitors.NodeElements;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(String value) {
    super(new BasicType(TYPE.BOOL));
    this.value = value.equals("true");
  }

  public boolean value() {
    return value;
  }

  @Override
  public String toString() {
    return "BoolExpr(" + value + ")";
  }

}
