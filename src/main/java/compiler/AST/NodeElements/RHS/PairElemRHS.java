package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.Types.Type;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class PairElemRHS extends PairElem {

  public PairElemRHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitPairElemRHS(this);
  }
}
