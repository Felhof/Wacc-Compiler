package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.ListExpr;
import compiler.visitors.NodeElements.TypeList;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.SymbolTable;

public class FuncNode implements Node {
  private Type returnType;
  private String name;
  private ListExpr paramList;
  private ASTNode astNode;
  private SymbolTable ST;

  public FuncNode(Type returnType, String name,
      ListExpr paramList, ASTNode astNode, SymbolTable st) {
    this.returnType = returnType;
    this.name = name;
    this.paramList = paramList;
    this.astNode = astNode;
    ST = st;
  }

  @Override
  public String toString() {
    return "FuncNode(" + returnType.toString() +
        ", name:'" + name +
        ", paramList:" + paramList.toString() +
        ", astNode:" + astNode.toString() +
        ')';
  }
}
