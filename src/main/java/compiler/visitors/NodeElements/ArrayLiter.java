package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.ArrType;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public class ArrayLiter extends AssignRHS implements Returnable {
  private Expr[] elems;

  public ArrayLiter(Expr[] elems, Type elemType) {
    super(new ArrType(elemType));
    this.elems = elems;
  }

  public boolean isEmpty() {
    return elems.length == 0;
  }
}
