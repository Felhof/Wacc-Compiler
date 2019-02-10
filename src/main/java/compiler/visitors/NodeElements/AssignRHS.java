package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;

public abstract class AssignRHS implements Returnable {
  protected Type type;

  public Type type() {
    return type;
  }

}
