package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;

public class PrintNode extends Node {
  private boolean newLine;
  private Expr expr;

  public PrintNode(boolean newLine, Expr expr, int lineNumber) {
    super(lineNumber);
    this.newLine = newLine;
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "Print" + (newLine ? "ln " : " ") + expr.toString();
  }
}
