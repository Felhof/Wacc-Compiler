package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public abstract class AssignRHS implements Returnable {
  protected Type type;

  public AssignRHS(Type type) {
    this.type = type;
  }

  public Type type() {
    return type;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}
