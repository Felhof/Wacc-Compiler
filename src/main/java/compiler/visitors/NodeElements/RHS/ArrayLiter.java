package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.NodeElem;
import compiler.visitors.NodeElements.Types.ArrType;
import compiler.visitors.NodeElements.Types.Type;
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
