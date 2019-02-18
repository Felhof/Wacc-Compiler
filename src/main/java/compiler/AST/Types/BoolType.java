package compiler.AST.Types;

import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class BoolType extends Type {
  private static BoolType instance = new BoolType();

  private BoolType(){}

  public static BoolType getInstance() {
    return instance;
  }

  @Override
  public boolean equals(Type type) {
    return instance == type;
  }

  @Override
  public String toString() {
    return "BOOL";
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
