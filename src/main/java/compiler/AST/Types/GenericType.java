package compiler.AST.Types;

public class GenericType extends Type {

  public boolean equals(Type type) {
    return true;
  }


  @Override
  public String toString() {
    return "Any";
  }
}
