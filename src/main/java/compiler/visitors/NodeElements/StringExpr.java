package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.Type.TYPE;

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

}
