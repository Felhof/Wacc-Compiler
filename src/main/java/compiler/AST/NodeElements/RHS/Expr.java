package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;

public abstract class Expr extends NodeElem {
  protected boolean hasBrackets = false;

  public Expr(Type type) {
    super(type);
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }
}
