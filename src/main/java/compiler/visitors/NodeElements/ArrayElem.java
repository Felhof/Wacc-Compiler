package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.NodeElements.Types.Type;

public class ArrayElem extends Expr {

  private String varName;
  private Expr[] indexes;

  public ArrayElem(Type type, String varName, Expr[] indexes) {
    super(type);
    this.varName = varName;
    this.indexes = indexes;
  }

}
