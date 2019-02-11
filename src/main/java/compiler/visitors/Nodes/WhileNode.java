package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.SymbolTable;

public class WhileNode implements Node {

  private Expr condition;
  private ASTNode stat;
  private SymbolTable statST;

  public WhileNode(Expr condition, ASTNode stat, SymbolTable statST) {
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
