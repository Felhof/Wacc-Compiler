package compiler.visitors.NodeElements;

public abstract class AssignRHS {
  protected Type type;

  public Type type() {
    return type;
  }

  public abstract void setType();
}
