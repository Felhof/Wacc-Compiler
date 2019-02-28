package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.visitors.backend.NodeVisitor;

public class VarAssignNode extends Node {

  private NodeElem lhs;
  private NodeElem rhs;

  public VarAssignNode(NodeElem lhs,
      NodeElem rhs, int lineNumber) {
    super(lineNumber);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarAssignment" + lhs.toString()
        + "= " + rhs.toString();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitAssignNode(this);
  }

  public NodeElem rhs() {
    return rhs;
  }

  public NodeElem lhs() {
    return lhs;
  }
}
