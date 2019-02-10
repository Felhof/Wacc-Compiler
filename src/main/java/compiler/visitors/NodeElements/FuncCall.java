package compiler.visitors.NodeElements;

public class FuncCall extends AssignRHS {
  private String funcName;
  private TypeList argsList;

  public FuncCall(String funcName,
      TypeList argsList, Type returnType) {
    this.funcName = funcName;
    this.argsList = argsList;
    super.type = returnType;
  }
}
