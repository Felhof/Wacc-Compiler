package compiler.visitors.identifiers;

import compiler.visitors.Returnable;

public class Variable implements Identifier, Returnable {
  private TYPE type;

  public Variable(TYPE type) {
    this.type = type;
  }

  public TYPE type() {
    return type;
  }
}
