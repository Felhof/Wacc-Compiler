package compiler.visitors.NodeElements.Types;

public class BasicType extends Type {
  private TYPE type;

  public BasicType(TYPE type) {
    this.type = type;
  }

  @Override
  public boolean equals(Type type) {
    return type instanceof BasicType
        && this.type.equals(((BasicType) type).type());
  }

  public TYPE type() {
    return type;
  }

  @Override
  public int hashCode() {
    return type.ordinal();
  }

  @Override
  public String toString() {
    return type.value().toUpperCase();
  }
}
