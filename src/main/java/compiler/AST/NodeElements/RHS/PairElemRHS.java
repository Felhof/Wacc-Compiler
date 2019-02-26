package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class PairElemRHS extends PairElem {

  public PairElemRHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitPairElemRHS(this);
  }
}
