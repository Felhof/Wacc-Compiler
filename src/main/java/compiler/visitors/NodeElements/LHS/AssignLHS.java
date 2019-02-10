package compiler.visitors.NodeElements.LHS;

import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public abstract class AssignLHS implements Returnable {
  protected Type type;

  public AssignLHS(Type type) {
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
