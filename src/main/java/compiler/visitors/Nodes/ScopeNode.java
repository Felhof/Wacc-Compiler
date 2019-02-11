package compiler.visitors.Nodes;

import compiler.visitors.SymbolTable;

public class ScopeNode implements Node {

  public ScopeNode(ASTNode astNode,
      SymbolTable symbolTable) {
    this.astNode = astNode;
  }

  private ASTNode astNode;

  @Override
  public String toString() {
    return "NewScopeNode:" + astNode.toString();
  }
}
