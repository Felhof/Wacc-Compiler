package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public class ArrayElemLHS extends ArrayElem implements LHS {

  public ArrayElemLHS(Type type, String varName, Expr[] indexes) {
    super(type, varName, indexes);
  }

}
