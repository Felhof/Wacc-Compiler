package compiler.AST.NodeElements.RHS;

import compiler.AST.ASTData;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class ArrayLiter extends NodeElem implements ASTData {
  private Expr[] elems;

  public ArrayLiter(Expr[] elems, Type arrType) {
    super(arrType);
    this.elems = elems;
  }

  public boolean isEmpty() {
    return elems.length == 0;
  }

  public Expr[] elems() {
    return elems;
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitArrayLiter(this);
  }
}
