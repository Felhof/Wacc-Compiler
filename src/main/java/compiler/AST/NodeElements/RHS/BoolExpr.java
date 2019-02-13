package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.BoolType;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(String value) {
    super(BoolType.getInstance());
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
