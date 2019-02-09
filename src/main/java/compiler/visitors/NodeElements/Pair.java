package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;

public class Pair extends AssignRHS implements Returnable {

  Expr fstExpr;
  Expr sndExpr;

  public Pair(Expr fstExpr, Expr sndExpr) {
    this.fstExpr = fstExpr;
    this.sndExpr = sndExpr;
    this.type = new PairType(fstExpr.type(), fstExpr.type());
  }

}
