package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class ArrayElem extends LHS {

  private String varName;
  private Expr[] indexes;

  public ArrayElem(Type type, String varName, Expr[] indexes) {
    super(type);
    this.varName = varName;
    this.indexes = indexes;
  }

  public Expr[] indexes() {
    return indexes;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitArrayElem(this);
  }

  @Override
  public String varName() {
    return varName;
  }

  @Override
  public int sizeOf() {
    // TODO: To think about
    return 0;
  }
}
