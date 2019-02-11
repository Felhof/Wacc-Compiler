package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;

public class IntExpr extends Expr {
  private int value;

  public IntExpr(String value) {
    super(new BasicType(BasicType.TYPE.INT));
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
