package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.Type.TYPE;

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

}
