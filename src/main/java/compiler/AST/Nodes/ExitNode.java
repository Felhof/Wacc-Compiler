package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

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

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    visitor.visitExit(this);
    return null;
  }

  public Expr exitStatus() {
    return exitStatus;
  }
}
