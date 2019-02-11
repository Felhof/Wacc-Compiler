package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;

public class IntExpr extends Expr {
  private String value;

  public IntExpr(String value) {
    super(new BasicType(BasicType.TYPE.INT));
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "IntExpr(" + value + ')';
  }

}
