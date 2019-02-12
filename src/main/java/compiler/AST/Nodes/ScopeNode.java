package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;

public class ScopeNode implements Node {

  SymbolTable symbolTable;

  public ScopeNode(ASTNode astNode,
      SymbolTable symbolTable) {
    this.astNode = astNode;
    this.symbolTable = symbolTable;
  }

  private ASTNode astNode;

  @Override
  public String toString() {
    return "NewScopeNode:" + astNode.toString();
  }
}
