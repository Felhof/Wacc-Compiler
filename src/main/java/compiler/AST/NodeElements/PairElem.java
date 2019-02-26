package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public abstract class PairElem extends Expr {

  private Expr expr;
  private int posInPair;

  public PairElem(Type type, Expr expr, int posInPair) {
    super(type);
    this.expr = expr;
    this.posInPair = posInPair;
  }

  @Override
  public int sizeOf() {
    // todo
    return 0;
  }

  public Expr expr() {
    return expr;
  }

  public int posInPair() {
    return posInPair;
  }
}
