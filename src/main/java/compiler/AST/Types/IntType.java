package compiler.AST.Types;

import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class IntType extends Type {
  private static IntType instance = new IntType();

  private IntType(){}

  public static IntType getInstance() {
    return instance;
  }

  @Override
  public boolean equals(Type type) {
    return instance == type;
  }

  @Override
  public String toString() {
    return "INT";
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
