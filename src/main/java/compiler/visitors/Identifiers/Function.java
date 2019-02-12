package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.ListExpr;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;

public class Function implements Identifier, Returnable {
  private ListExpr paramList;
  private Type returnType;

  public Function(ListExpr paramList,
      Type returnType) {
    this.paramList = paramList;
    this.returnType = returnType;
  }

  public ListExpr getParamList() {
    return paramList;
  }

  public Type getType() {
    return returnType;
  }
}
