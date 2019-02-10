package compiler.visitors.Identifiers;

import compiler.visitors.NodeElements.Type;
import java.util.List;

public class Function {
  private List<Type> paramList;
  private Type returnType;

  public Function(List<Type> paramList,
      Type returnType) {
    this.paramList = paramList;
    this.returnType = returnType;
  }

  public List<Type> getParamList() {
    return paramList;
  }
}
