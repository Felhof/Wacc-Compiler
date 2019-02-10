package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.RHS.Expr;

public class ReturnNode implements Node {
  private Expr expr;

  public ReturnNode(Expr expr) {
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "ReturnNode(" + expr.toString() + ')';
  }
}
