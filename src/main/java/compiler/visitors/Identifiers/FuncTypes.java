package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;
import java.util.List;

public class FuncTypes implements Returnable {
  private List<Type> paramTypeList;
  private Type returnType;

  public FuncTypes(List<Type> paramTypeList,
      Type returnType) {
    this.paramTypeList = paramTypeList;
    this.returnType = returnType;
  }

  public List<Type> getParamTypes() {
    return paramTypeList;
  }

  public Type getReturnType() {
    return returnType;
  }
}
