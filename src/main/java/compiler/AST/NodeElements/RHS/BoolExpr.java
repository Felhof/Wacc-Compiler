package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.BoolType;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class BoolExpr extends Expr {
  private boolean value;

  public BoolExpr(String value) {
    super(BoolType.getInstance());
    this.value = value.equals("true");
  }

  public boolean value() {
    return value;
  }

  @Override
  public String toString() {
    return "BoolExpr(" + value + ")";
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
