package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.CharType;
import compiler.IR.Instructions.STRING_FIELD;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class CharExpr extends Expr {

  private String value;
  private boolean isEscapeChar;

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
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitCharExpr(this);
  }
}
