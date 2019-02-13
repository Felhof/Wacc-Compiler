package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;

public class ReadNode extends Node {

  private NodeElem lhs;

  public ReadNode(NodeElem lhs, int lineNumber) {
    super(lineNumber);
    this.lhs = lhs;
  }

  @Override
  public String toString() {
    return "ReadNode( " + lhs.toString() + ')';
  }
}
