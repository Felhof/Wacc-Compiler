package compiler.visitors.Nodes;

import compiler.visitors.identifiers.Expr;

public class IfElseNode implements Node {
  private Expr condition;
  private ASTNode thenStat;
  private ASTNode elseStat;

  public IfElseNode(Expr condition, ASTNode thenStat,
      ASTNode elseStat) {
    this.condition = condition;
    this.thenStat = thenStat;
    this.elseStat = elseStat;
  }

  @Override
  public String toString() {
    return "If (" + condition + ")" +
        "\n then: {" + thenStat.toString() + '}' +
        "\n else: {" + elseStat.toString()+ '}';
  }
}
