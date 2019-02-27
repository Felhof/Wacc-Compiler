package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.IntType;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

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
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitIntExpr(this);
  }

  public void setNegative() {
    value = "-" + value;
  }
}
