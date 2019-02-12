package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.ListExpr;
import compiler.visitors.NodeElements.NodeElem;
import compiler.visitors.NodeElements.Types.Type;

public class FuncCall extends NodeElem {
  private String funcName;
  private ListExpr argsList;

  public FuncCall(String funcName,
      ListExpr argsList, Type returnType) {
    super(returnType);
    this.funcName = funcName;
    this.argsList = argsList;
  }

  @Override
  public String toString() {
    return "FuncCall:" +
        "funcName:" + funcName +
        ", arguments" + argsList.toString();
  }
}
