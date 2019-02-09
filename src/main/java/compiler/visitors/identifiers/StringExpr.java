package compiler.visitors.identifiers;

public class StringExpr extends Expr {

  private final String value;

  public StringExpr(String value) {
    super(TYPE.STRING);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StringExpr(" + value + ')';
  }
}
