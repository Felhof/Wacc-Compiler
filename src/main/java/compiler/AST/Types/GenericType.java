package compiler.AST.Types;

import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class GenericType extends Type {

  private static GenericType instance = new GenericType();

  private GenericType() {}

  public static GenericType getInstance() {
    return instance;
  }

  public boolean equals(Type type) {
    return true;
  }

  @Override
  public String toString() {
    return "ANY";
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
