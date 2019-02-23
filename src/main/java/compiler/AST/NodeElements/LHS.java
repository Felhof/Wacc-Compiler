package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public abstract class LHS extends Expr {

  public LHS(Type type) {
    super(type);
  }

  public abstract String varName();

}
