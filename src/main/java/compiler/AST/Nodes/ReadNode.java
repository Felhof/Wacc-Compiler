package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class ReadNode extends Node {

  private NodeElem lhs;

  public ReadNode(Expr lhs, int lineNumber) {
    super(lineNumber);
    this.lhs = lhs;
  }

  public NodeElem lhs(){ return lhs; }

  @Override
  public String toString() {
    return "ReadNode( " + lhs.toString() + ')';
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitReadExpr(this);
  }
}
