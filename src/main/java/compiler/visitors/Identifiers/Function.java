package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;
import java.util.List;

public class Function implements Identifier, Returnable {
  private List<Type> paramTypeList;
  private Type returnType;

  public Function(List<Type> paramTypeList,
      Type returnType) {
    this.paramTypeList = paramTypeList;
    this.returnType = returnType;
  }

  public List<Type> getParamTypes() {
    return paramTypeList;
  }

  public Type getType() {
    return returnType;
  }
}
