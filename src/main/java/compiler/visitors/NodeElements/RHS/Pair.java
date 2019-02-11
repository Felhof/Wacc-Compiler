package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.PairType;
import compiler.visitors.Returnable;

public class Pair extends Expr implements Returnable {

  private Expr fstExpr;
  private Expr sndExpr;

  public Pair(Expr fstExpr, Expr sndExpr) {
    super(new PairType(fstExpr.type(), sndExpr.type()));
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

}
