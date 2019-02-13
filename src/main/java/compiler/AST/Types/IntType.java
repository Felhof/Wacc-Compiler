package compiler.AST.Types;

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
}
