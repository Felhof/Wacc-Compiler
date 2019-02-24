package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class PairElem extends LHS {

  private Expr expr;
  private int posInPair;
  private boolean stackAssign;

  public PairElem(Type type, Expr expr, int posInPair) {
    super(type);
    this.expr = expr;
    this.posInPair = posInPair;
    this.stackAssign = stackAssign;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitPairExpr(this);
  }

  @Override
  public String varName() {
    return "";
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
