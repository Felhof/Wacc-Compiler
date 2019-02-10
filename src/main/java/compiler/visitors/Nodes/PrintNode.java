package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.Expr;

public class PrintNode implements Node {
  private boolean newLine;
  private Expr expr;

  public PrintNode(boolean newLine, Expr expr) {
    this.newLine = newLine;
    this.expr = expr;
  }
}
