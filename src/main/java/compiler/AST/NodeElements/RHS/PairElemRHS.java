package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class PairElemRHS extends PairElem {

  public PairElemRHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitPairElemRHS(this);
  }
}
