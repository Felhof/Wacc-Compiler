package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class PairElem extends LHS {

  private Expr expr;
  private int posInPair;

  public PairElem(Type type, Expr expr, int posInPair) {
    super(type);
    this.expr = expr;
    this.posInPair = posInPair;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
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
}
