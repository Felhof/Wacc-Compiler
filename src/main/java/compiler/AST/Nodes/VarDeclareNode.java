package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;

public class VarDeclareNode extends Node {
  private Type varType;
  private String varName;
  private NodeElem rhs;

  public VarDeclareNode(Type varType, String varName,
      NodeElem rhs, int lineNumber) {
    super(lineNumber);
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
