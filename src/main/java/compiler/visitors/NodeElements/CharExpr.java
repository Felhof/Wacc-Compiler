package compiler.visitors.NodeElements;

public class CharExpr extends Expr {

  private String value;

  public CharExpr(String value) {
    super(new BasicType(TYPE.CHAR));
    this.value = value.substring(1, value.length() - 1);

  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "CharExpr(" + value + ')';
  }

  @Override
  public void setType() {
  }
}
