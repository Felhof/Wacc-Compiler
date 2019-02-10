package compiler.visitors.NodeElements.Types;

public class ArrType extends Type {

  private Type type;

  public ArrType(Type type) {
    this.type = type;
  }

  public Type type() {
    return type;
  }

  @Override
  public boolean equals(Type type) {
    return type instanceof ArrType && this.type.equals(((ArrType) type).type());
  }

  @Override
  public String toString() {
    return "ArrType(" + type.toString() + ')';
  }
}
