package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.ArrType;
import compiler.AST.Types.CharType;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class StringExpr extends Expr {

  private final String value;

  public StringExpr(String value) {
    super(new ArrType(CharType.getInstance()));
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StringExpr(" + value + ')';
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitStringExpr(this);
  }
}
