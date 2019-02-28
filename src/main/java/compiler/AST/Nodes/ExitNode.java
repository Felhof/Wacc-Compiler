package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.visitors.backend.NodeVisitor;

public class ExitNode extends Node {
  private Expr exitStatus;

  public ExitNode(Expr exitStatus, int lineNumber) {
    super(lineNumber);
    this.exitStatus = exitStatus;
  }

  @Override
  public String toString() {
    return "ExitNode(" + exitStatus.toString() + ')';
  }

  public Expr exitStatus() {
    return exitStatus;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitExitNode(this);
  }

}
