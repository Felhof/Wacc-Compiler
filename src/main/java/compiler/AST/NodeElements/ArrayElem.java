package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;

public class ArrayElem extends Expr {

  private String varName;
  private Expr[] indexes;

  public ArrayElem(Type type, String varName, Expr[] indexes) {
    super(type);
    this.varName = varName;
    this.indexes = indexes;
  }

}