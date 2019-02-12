package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;

public class ExitNode implements Node {
  private Expr exitStatus;

  public ExitNode(Expr exitStatus) {
    this.exitStatus = exitStatus;
  }

  @Override
  public String toString() {
    return "ExitNode(" + exitStatus.toString() + ')';
  }
}
