package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

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

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
