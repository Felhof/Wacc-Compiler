package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.AssignRHS;

public class VarDeclareNode implements Node {
  private String varName;
  private AssignRHS rhs;

  public VarDeclareNode(String varName,
      AssignRHS rhs) {
    this.varName = varName;
    this.rhs = rhs;
  }

  @Override
  public String toString() {




    return "VarDeclaration: " + varName + " = " + rhs.toString();
  }
}
