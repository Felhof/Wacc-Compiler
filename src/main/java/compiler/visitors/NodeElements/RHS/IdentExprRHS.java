package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.Type;

public class IdentExprRHS extends Expr {

  private String varName;

  public IdentExprRHS(String varName, Type type) {
    super(type);
    this.varName = varName;
  }

  @Override
  public String toString() {
    return "IdentExprRHS: " +
         varName;
  }
}
