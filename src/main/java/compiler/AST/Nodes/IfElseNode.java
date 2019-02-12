package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.SymbolTable.SymbolTable;

public class IfElseNode implements Node {
  private Expr condition;
  private ParentNode thenStat;
  private ParentNode elseStat;
  private SymbolTable thenST;
  private SymbolTable elseST;

  public IfElseNode(Expr condition, ParentNode thenStat, SymbolTable thenST,
      ParentNode elseStat, SymbolTable elseST) {
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
