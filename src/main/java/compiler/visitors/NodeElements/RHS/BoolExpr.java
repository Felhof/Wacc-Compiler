package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(String value) {
    super(new BasicType(BasicType.TYPE.BOOL));
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
