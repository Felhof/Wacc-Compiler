package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.TypeList;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public class Function implements Identifier, Returnable {
  private TypeList paramList;
  private Type returnType;

  public Function(TypeList paramList,
      Type returnType) {
    this.paramList = paramList;
    this.returnType = returnType;
  }

  public TypeList getParamList() {
    return paramList;
  }

  public Type getType() {
    return returnType;
  }
}
