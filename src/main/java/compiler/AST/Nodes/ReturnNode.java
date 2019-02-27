package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.visitors.backend.ASTVisitor;

public class ReturnNode extends Node {
  private Expr expr;

  public ReturnNode(Expr expr, int lineNumber) {
    super(lineNumber);
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "ReturnNode(" + expr.toString() + ')';
  }

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visitReturnNode(this);
  }

  public Expr expr() {
    return expr;
  }
}
