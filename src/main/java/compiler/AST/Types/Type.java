package compiler.AST.Types;

import compiler.visitors.ASTData;

public abstract class Type implements ASTData {

  public boolean equals(Type type) {
    return false;
  }

  public static Type getBasicType(String type) {
    switch (type) {
      case "int":
        return IntType.getInstance();
      case "char":
        return CharType.getInstance();
      case "bool":
        return BoolType.getInstance();
      default:
        return GenericType.getInstance();
    }
  }
}
