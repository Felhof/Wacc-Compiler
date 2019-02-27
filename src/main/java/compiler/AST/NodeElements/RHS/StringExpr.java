package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.ArrType;
import compiler.AST.Types.CharType;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

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
  public REG accept(ASTVisitor visitor) {
    return visitor.visitStringExpr(this);
  }

//  @Override
//  public int sizeOf() {
//    return (value.length() - 2 - STRING_FIELD.nbSpecialChar(value));
//  }
}
