package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.RHS.Expr;

public class FreeNode implements Node{

  private Expr freeExpr;

  public FreeNode(Expr freeExpr) {
    this.freeExpr = freeExpr;
  }

  @Override
  public String toString(){ return "Freenode(" + freeExpr.toString() + ")"; }
}
