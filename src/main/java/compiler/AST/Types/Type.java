package compiler.AST.Types;

import compiler.AST.ASTData;

public abstract class Type implements ASTData {

  public static final int WORD_SIZE = 4;
  public static final int BYTE_SIZE = 1;

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

  public boolean isByteSize() {
    // useful in code generation when loading elements
    return false;
  }

  public int getSize() {
    return isByteSize() ? BYTE_SIZE : WORD_SIZE;
  }
}
