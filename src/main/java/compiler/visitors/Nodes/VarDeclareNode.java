package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.NodeElem;
import compiler.visitors.NodeElements.Types.Type;

public class VarDeclareNode implements Node {
  private Type varType;
  private String varName;
  private NodeElem rhs;

  public VarDeclareNode(Type varType, String varName,
      NodeElem rhs) {
    this.varType = varType;
    this.varName = varName;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarDeclaration: " + varType.toString() + " "
        + varName + " = " + rhs.toString();
  }
}
