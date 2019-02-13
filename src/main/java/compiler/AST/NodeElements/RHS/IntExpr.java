package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.IntType;

public class IntExpr extends Expr {
  private String value;

  public IntExpr(String value) {
    super(IntType.getInstance());
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
