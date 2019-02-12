package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.NodeElements.Types.Type;

public class Ident extends Expr {

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
}
