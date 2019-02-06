package compiler.visitors.identifiers;

public class Variable implements Identifier {
  private Type type;

  public Variable(Type type) {
    this.type = type;
  }
}
