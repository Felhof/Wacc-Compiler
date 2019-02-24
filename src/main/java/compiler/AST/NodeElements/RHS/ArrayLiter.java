package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.visitors.ASTData;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

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
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitArrayLiter(this);
  }
}
