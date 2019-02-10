package compiler.visitors.NodeElements.Types;

public class ArrType implements Type {

  private Type type;

  public ArrType(Type type) {
    this.type = type;
  }

  @Override
  public boolean equals(Type type) {
    return this.type.equals(type);
  }
}
