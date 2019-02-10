package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public abstract class Expr extends AssignRHS implements Returnable {
  protected boolean hasBrackets = false;

  public Expr(Type type) {
    this.type = type;
  }

  public void putBrackets() {
    this.hasBrackets = true;
  }

}
