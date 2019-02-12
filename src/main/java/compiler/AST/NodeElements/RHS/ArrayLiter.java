package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.visitors.Returnable;

public class ArrayLiter extends NodeElem implements Returnable {
  private Expr[] elems;

  public ArrayLiter(Expr[] elems, Type arrType) {
    super(arrType);
    this.elems = elems;
  }

  public boolean isEmpty() {
    return elems.length == 0;
  }

}
