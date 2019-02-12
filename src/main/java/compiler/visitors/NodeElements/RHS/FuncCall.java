package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.NodeElem;
import compiler.visitors.NodeElements.TypeList;
import compiler.visitors.NodeElements.Types.Type;

public class FuncCall extends NodeElem {
  private String funcName;
  private TypeList argsList;

  public FuncCall(String funcName,
      TypeList argsList, Type returnType) {
    super(returnType);
    this.funcName = funcName;
    this.argsList = argsList;
  }
}