package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class PairElemLHS extends PairElem {

  public PairElemLHS(Type type,
      Expr expr, int posInPair) {
    super(type, expr, posInPair);
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitPairElemLHS(this);
  }
}
