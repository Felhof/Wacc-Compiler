package compiler.visitors.identifiers;

class Variable implements Identifier {
  private Type type;

  public Variable(Type type) {
    this.type = type;
  }
}
