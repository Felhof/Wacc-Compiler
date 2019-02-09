package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;

public class BasicType implements Type, Returnable {
  private TYPE type;

  public BasicType(TYPE type) {
    this.type = type;
  }

  @Override
  public boolean equals(Type type) {
    return type instanceof BasicType
        && this.type.equals(((BasicType) type).type);
  }
}
