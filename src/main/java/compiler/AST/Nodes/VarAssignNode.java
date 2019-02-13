package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;

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
}
