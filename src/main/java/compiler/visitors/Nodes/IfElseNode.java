package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.Expr;

public class IfElseNode implements Node {
  private Expr condition;
  private ParentNode thenStat;
  private ParentNode elseStat;

  public IfElseNode(Expr condition, ParentNode thenStat,
      ParentNode elseStat) {
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
