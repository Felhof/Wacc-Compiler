package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;

public class IdentRHS extends Ident {

  public IdentRHS(String varName, Type type) {
    super(varName, type);
  }
}
