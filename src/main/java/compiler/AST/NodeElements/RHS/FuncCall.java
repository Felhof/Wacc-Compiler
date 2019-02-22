package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

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

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitFuncCall(this);
  }

  public String funcName() {
    return funcName;
  }

  public ListExpr argsList() {
    return argsList;
  }
}
