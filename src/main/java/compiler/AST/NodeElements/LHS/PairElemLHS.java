package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public class PairElemLHS extends PairElem implements LHS {

  public PairElemLHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }
}
