package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public class PairElem extends NodeElem {

  private Expr expr;
  private int posInPair;

  public PairElem(Type type, Expr expr, int posInPair) {
    super(type);
    this.expr = expr;
    this.posInPair = posInPair;
  }
}
