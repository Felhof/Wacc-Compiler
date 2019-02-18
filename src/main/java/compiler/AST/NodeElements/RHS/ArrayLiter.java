package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.visitors.ASTData;

public class ArrayLiter extends NodeElem implements ASTData {
  private Expr[] elems;

  public ArrayLiter(Expr[] elems, Type arrType) {
    super(arrType);
    this.elems = elems;
  }

  public boolean isEmpty() {
    return elems.length == 0;
  }

}
