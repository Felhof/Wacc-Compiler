package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.PairType;
import compiler.visitors.ASTData;

public class Pair extends Expr implements ASTData {

  private Expr fstExpr;
  private Expr sndExpr;

  public Pair(Expr fstExpr, Expr sndExpr) {
    super(new PairType(fstExpr.type(), sndExpr.type()));
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

  @Override
  public String toString() {
    return "Pair" +
        "["+ fstExpr.toString() +
        ", " + sndExpr.toString() +
        "]";
  }
}
