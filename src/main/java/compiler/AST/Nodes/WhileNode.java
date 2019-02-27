package compiler.AST.Nodes;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.IR.Operand.REG;
import compiler.visitors.ASTVisitor;

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

  public Expr condition() {
    return condition;
  }

  public ParentNode parentNode() {
    return stat;
  }

  @Override
  public String toString() {
    return "While (" + condition + ")" +
        "\n {" + stat.toString() + '}';
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitWhileNode(this);
  }

  public SymbolTable statST() {
    return statST;
  }
}
