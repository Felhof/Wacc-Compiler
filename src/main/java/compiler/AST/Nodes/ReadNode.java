package compiler.AST.Nodes;

import compiler.AST.NodeElements.LHS;
import compiler.AST.NodeElements.NodeElem;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class ReadNode extends Node {

  private LHS lhs;

  public ReadNode(LHS lhs, int lineNumber) {
    super(lineNumber);
    this.lhs = lhs;
  }

  public LHS lhs(){ return lhs; }

  @Override
  public String toString() {
    return "ReadNode( " + lhs.toString() + ')';
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitReadExpr(this);
  }
}
