package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;

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
