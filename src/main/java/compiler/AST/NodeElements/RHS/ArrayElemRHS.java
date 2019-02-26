package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.Types.Type;

public class ArrayElemRHS extends ArrayElem {

  public ArrayElemRHS(Type type, String varName,
      Expr[] indexes) {
    super(type, varName, indexes);
  }
}
