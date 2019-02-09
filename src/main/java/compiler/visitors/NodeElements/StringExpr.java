package compiler.visitors.NodeElements;

public class StringExpr extends Expr {

  private final String value;

  public StringExpr(String value) {
    super(new BasicType(TYPE.STRING));
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StringExpr(" + value + ')';
  }

  @Override
  public void setType() {

  }
}
