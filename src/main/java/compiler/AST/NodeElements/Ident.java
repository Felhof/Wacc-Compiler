package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.Type;

public abstract class Ident extends Expr {

  private String varName;

  public Ident(String varName, Type type) {
    super(type);
    this.varName = varName;
  }

  @Override
  public String toString() {
    return "Ident: " +
        varName;
  }

  public String varName() {
    return varName;
  }
}
