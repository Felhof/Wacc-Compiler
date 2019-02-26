package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;

public class IdentLHS extends Ident implements LHS {

  public IdentLHS(String varName, Type type) {
    super(varName, type);
  }
}
