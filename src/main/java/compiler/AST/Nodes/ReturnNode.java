package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

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
  public REG accept(ASTVisitor visitor) {
    return visitor.visitReturn(this);
  }

  public Expr expr() {
    return expr;
  }
}
