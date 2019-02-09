package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.Expr;
import compiler.visitors.SymbolTable;

public class WhileNode implements Node {

  Expr condition;
  ASTNode stat;
  SymbolTable statST;

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
