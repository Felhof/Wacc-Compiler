package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.Expr;
import compiler.visitors.SymbolTable;

public class IfElseNode implements Node {
  private Expr condition;
  private ASTNode thenStat;
  private ASTNode elseStat;
  private SymbolTable thenST;
  private SymbolTable elseST;

  public IfElseNode(Expr condition, ASTNode thenStat, SymbolTable thenST,
      ASTNode elseStat, SymbolTable elseST) {
    this.condition = condition;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
    this.thenST = thenST;
    this.elseST = elseST;
  }

  @Override
  public String toString() {
    return "If (" + condition + ")" +
        "\n then: {" + thenStat.toString() + '}' +
        "\n else: {" + elseStat.toString()+ '}';
  }
}
