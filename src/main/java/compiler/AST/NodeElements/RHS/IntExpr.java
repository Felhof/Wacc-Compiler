package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.IntType;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class IntExpr extends Expr {
  private String value;

  public IntExpr(String value) {
    super(IntType.getInstance());
    this.value = value;
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return "IntExpr(" + value + ')';
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitIntExpr(this);
  }
}
