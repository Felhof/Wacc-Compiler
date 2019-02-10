package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.Type;

public abstract class AssignRHS {
  protected Type type;

  public Type type() {
    return type;
  }

}
