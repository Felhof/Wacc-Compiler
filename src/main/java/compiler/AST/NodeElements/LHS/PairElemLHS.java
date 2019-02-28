package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class PairElemLHS extends PairElem {

  public PairElemLHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitPairElemLHS(this);
  }
}
