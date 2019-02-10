package compiler.visitors.NodeElements;

import java.util.List;

public class FuncCall extends AssignRHS {
  private String funcName;
  private TypeList argsList;

  public FuncCall(String funcName,
      TypeList argsList) {
    this.funcName = funcName;
    this.argsList = argsList;
  }
}
