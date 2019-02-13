package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;

public class ScopeNode extends Node {

  SymbolTable symbolTable;

  public ScopeNode(ParentNode parentNode,
      SymbolTable symbolTable, int lineNumber) {
    super(lineNumber);
    this.parentNode = parentNode;
    this.symbolTable = symbolTable;
  }

  private ParentNode parentNode;

  @Override
  public String toString() {
    return "NewScopeNode:" + parentNode.toString();
  }
}
