package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.LHS.AssignLHS;

public class ReadNode implements Node {

  private AssignLHS lhs;

  public ReadNode(AssignLHS lhs) {
    this.lhs = lhs;
  }

  @Override
  public String toString() {
    return "ReadNode( " + lhs.toString() + ')';
  }
}
