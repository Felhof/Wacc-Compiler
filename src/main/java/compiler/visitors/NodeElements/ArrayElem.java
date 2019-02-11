package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.Returnable;

public class ArrayElem implements Returnable {

  private String varName;
  private Expr[] indexes;

  public ArrayElem(String varName, Expr[] indexes) {
    this.varName = varName;
    this.indexes = indexes;
  }

}
