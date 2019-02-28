package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.visitors.backend.NodeVisitor;

public class PrintNode extends Node {
  private boolean newLine;
  private Expr expr;

  public PrintNode(boolean newLine, Expr expr, int lineNumber) {
    super(lineNumber);
    this.newLine = newLine;
    this.expr = expr;
  }

  public Expr expr() {
    return expr;
  }

  public boolean newLine() {return newLine; }

  @Override
  public String toString() {
    return "Print" + (newLine ? "ln " : " ") + expr.toString();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitPrintNode(this);
  }
}
