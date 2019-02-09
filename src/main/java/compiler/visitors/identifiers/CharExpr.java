package compiler.visitors.identifiers;

public class CharExpr extends Expr {

  private String value;

  public CharExpr(String value) {
    super(TYPE.CHAR);
    this.value = value.substring(1, value.length() - 1);

  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "CharExpr(" + value + ')';
  }
}
