package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.SymbolTable.SymbolTable;

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
