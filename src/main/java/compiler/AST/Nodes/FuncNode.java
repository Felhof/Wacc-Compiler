package compiler.AST.Nodes;

import compiler.AST.NodeElements.ListExpr;
import compiler.AST.Types.Type;
import compiler.AST.SymbolTable.SymbolTable;

public class FuncNode implements Node {
  private Type returnType;
  private String name;
  private ListExpr paramList;
  private ParentNode parentNode;
  private SymbolTable ST;

  public FuncNode(Type returnType, String name,
      ListExpr paramList, ParentNode parentNode, SymbolTable st) {
    this.returnType = returnType;
    this.name = name;
    this.paramList = paramList;
    this.parentNode = parentNode;
    ST = st;
  }

  @Override
  public String toString() {
    return "FuncNode(" + returnType.toString() +
        ", name:'" + name +
        ", paramList:" + paramList.toString() +
        ", parentNode:" + parentNode.toString() +
        ')';
  }
}
