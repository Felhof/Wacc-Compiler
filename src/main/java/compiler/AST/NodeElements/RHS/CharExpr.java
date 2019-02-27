package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.CharType;
import compiler.instr.REG;
import compiler.instr.STRING_FIELD;
import compiler.visitors.ASTVisitor;

public class CharExpr extends Expr {

  private String value;
  private boolean isEscapeChar = false;

  public CharExpr(String value) {
    super(CharType.getInstance());
    this.value = value.substring(1, value.length() - 1);
    isEscapeChar = STRING_FIELD.escape.contains(this.value);
  }

  public String value() {
    return (isEscapeChar ? String
      .valueOf((int) STRING_FIELD.escapeChars.get(STRING_FIELD.escape.indexOf(value))) : value);
  }

  public boolean isEscapeChar() {
    return isEscapeChar;
  }

  @Override
  public String toString() {
    return "CharExpr(" + value + ')';
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitCharExpr(this);
  }
}
