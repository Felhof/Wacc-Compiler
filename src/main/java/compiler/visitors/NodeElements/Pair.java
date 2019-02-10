package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.PairType;
import compiler.visitors.Returnable;

public class Pair extends AssignRHS implements Returnable {

  Expr fstExpr;
  Expr sndExpr;

  public Pair(Expr fstExpr, Expr sndExpr) {
    super(new PairType(fstExpr.type(), sndExpr.type()));
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
  }

}
