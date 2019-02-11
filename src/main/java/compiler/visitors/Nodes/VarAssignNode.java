package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.NodeElem;

public class VarAssignNode implements Node {

  private NodeElem lhs;
  private NodeElem rhs;

  public VarAssignNode(NodeElem lhs,
      NodeElem rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarAssignment" + lhs.toString()
        + "= " + rhs.toString();
  }
}
