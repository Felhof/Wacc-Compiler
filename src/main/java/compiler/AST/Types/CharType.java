package compiler.AST.Types;

public class CharType extends Type {
  private static CharType instance = new CharType();

  private CharType(){}

  public static CharType getInstance() {
    return instance;
  }

  @Override
  public boolean equals(Type type) {
    return instance == type;
  }

  @Override
  public String toString() {
    return "CHAR";
  }
}
