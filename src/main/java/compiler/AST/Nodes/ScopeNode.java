package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;

public class ScopeNode implements Node {

  SymbolTable symbolTable;

  public ScopeNode(ParentNode parentNode,
      SymbolTable symbolTable) {
    this.parentNode = parentNode;
    this.symbolTable = symbolTable;
  }

  private ParentNode parentNode;

  @Override
  public String toString() {
    return "NewScopeNode:" + parentNode.toString();
  }
}
