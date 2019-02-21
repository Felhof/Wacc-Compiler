package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.CharType;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class CharExpr extends Expr {

  private String value;

  public CharExpr(String value) {
    super(CharType.getInstance());
    this.value = value.substring(1, value.length() - 1);

  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "CharExpr(" + value + ')';
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitCharExpr(this);
  }

  @Override
  public int sizeOf() {
    return 1;
  }
}
