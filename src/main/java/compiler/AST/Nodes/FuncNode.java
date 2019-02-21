package compiler.AST.Nodes;

import compiler.AST.NodeElements.ListExpr;
import compiler.AST.Types.Type;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.ASTData;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class FuncNode extends Node {
  private Type returnType;
  private String name;
  private ListExpr paramList;
  private ParentNode parentNode;
  private SymbolTable symbolTable;

  public FuncNode(Type returnType, String name,
      ListExpr paramList, ParentNode parentNode, SymbolTable st, int lineNumber) {
    super(lineNumber);
    this.returnType = returnType;
    this.name = name;
    this.paramList = paramList;
    this.parentNode = parentNode;
    symbolTable = st;
  }

  @Override
  public String toString() {
    return "FuncNode(" + returnType.toString() +
        ", name:'" + name +
        ", paramList:" + paramList.toString() +
        ", parentNode:" + parentNode.toString() +
        ')';
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitFuncNode(this);
  }

  public SymbolTable symbolTable() {
    return symbolTable;
  }

  public String name() {
    return name;
  }

  public ParentNode getParentNode() {
    return parentNode;
  }

  public ListExpr paramList() {
    return paramList;
  }
}
