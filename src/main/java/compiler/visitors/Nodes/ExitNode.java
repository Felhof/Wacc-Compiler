package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.RHS.Expr;

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
