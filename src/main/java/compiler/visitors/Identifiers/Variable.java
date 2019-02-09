package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.Type;
import compiler.visitors.Returnable;

public class Variable implements Identifier, Returnable {
  private Type type;

  public Variable(Type type) {
    this.type = type;
  }

  public Type type() {
    return type;
  }
}
