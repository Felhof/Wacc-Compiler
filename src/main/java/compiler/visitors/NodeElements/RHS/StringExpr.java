package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.ArrType;
import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.BasicType.TYPE;

public class StringExpr extends Expr {

  private final String value;

  public StringExpr(String value) {
    super(new ArrType(new BasicType(TYPE.CHAR)));
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
