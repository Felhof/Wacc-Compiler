package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.SymbolTable.SymbolTable;

public class WhileNode extends Node {

  private Expr condition;
  private ParentNode stat;
  private SymbolTable statST;

  public WhileNode(Expr condition, ParentNode stat, SymbolTable statST, int lineNumber) {
    super(lineNumber);
    this.condition = condition;
    this.stat = stat;
    this.statST = statST;
  }

  @Override
  public String toString() {
    return "While (" + condition + ")" +
        "\n {" + stat.toString() + '}';
  }

}
