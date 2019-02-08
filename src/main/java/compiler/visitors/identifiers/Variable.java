package compiler.visitors.identifiers;

public class Variable implements Identifier {
  private TYPE type;

  public Variable(TYPE type) {
    this.type = type;
  }

  public TYPE type() {
    return type;
  }
}
