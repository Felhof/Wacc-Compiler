package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.PairType;
import compiler.IR.Operand.REG;
import compiler.AST.ASTData;
import compiler.visitors.backend.NodeElemVisitor;

public class Pair extends Expr implements ASTData {

  private Expr fstExpr;
  private Expr sndExpr;

  public Pair(Expr fstExpr, Expr sndExpr) {
    super(new PairType(fstExpr.type(), sndExpr.type()));
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

  public Expr fst() {
    return fstExpr;
  }

  public Expr snd() {
    return sndExpr;
  }

  @Override
  public String toString() {
    return "Pair" +
        "[" + fstExpr.toString() +
        ", " + sndExpr.toString() +
        "]";
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitPair(this);
  }
}
