package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class FreeNode extends Node{

  private Expr freeExpr;

  public FreeNode(Expr freeExpr, int lineNumber) {
    super(lineNumber);
    this.freeExpr = freeExpr;
  }

  @Override
  public String toString(){ return "Freenode(" + freeExpr.toString() + ")"; }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitFreeNode(this);
  }

  public Expr freeExpr() {
    return freeExpr;
  }
}
