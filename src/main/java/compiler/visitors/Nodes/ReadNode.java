package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.NodeElem;

public class ReadNode implements Node {

  private NodeElem lhs;

  public ReadNode(NodeElem lhs) {
    this.lhs = lhs;
  }

  @Override
  public String toString() {
    return "ReadNode( " + lhs.toString() + ')';
  }
}
