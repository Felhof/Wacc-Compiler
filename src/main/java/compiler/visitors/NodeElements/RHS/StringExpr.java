package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;

public class StringExpr extends Expr {

  private final String value;

  public StringExpr(String value) {
    super(new BasicType(BasicType.TYPE.STRING));
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
