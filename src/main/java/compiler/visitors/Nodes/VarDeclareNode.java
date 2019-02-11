package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.NodeElem;

public class VarDeclareNode implements Node {
  private String varName;
  private NodeElem rhs;

  public VarDeclareNode(String varName,
      NodeElem rhs) {
    this.varName = varName;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarDeclaration: " + varName + " = " + rhs.toString();
  }
}
