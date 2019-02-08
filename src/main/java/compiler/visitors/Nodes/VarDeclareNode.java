package compiler.visitors.Nodes;

import compiler.visitors.identifiers.Identifier;

public class VarDeclareNode implements Node {
  private String varName;
  private Identifier rhs;

  public VarDeclareNode(String varName,
      Identifier rhs) {
    this.varName = varName;
    this.rhs = rhs;
  }

  @Override
  public String toString() {




    return "VarDeclaration: " + varName + " = " + rhs.toString();
  }
}
