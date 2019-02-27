package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.backend.ASTVisitor;

public class IfElseNode extends Node {
  private Expr condition;
  private ParentNode thenStat;
  private ParentNode elseStat;
  private SymbolTable thenST;
  private SymbolTable elseST;


  public IfElseNode(Expr condition, ParentNode thenStat, SymbolTable thenST,
      ParentNode elseStat, SymbolTable elseST, int lineNumber) {
    super(lineNumber);
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

  @Override
  public void accept(ASTVisitor visitor) {
    visitor.visitIfElseNode(this);
  }

  public SymbolTable thenST() {
    return thenST;
  }

  public SymbolTable elseST() {
    return elseST;
  }

  public Expr cond() {
    return condition;
  }

  public ParentNode thenStat() {
    return thenStat;
  }

  public ParentNode elseStat() {
    return elseStat;
  }

  public int thenStackOffset() {
    return thenST.getStackOffset();
  }

  public int elseStatOffset() {
    return elseST.getStackOffset();
  }
}
