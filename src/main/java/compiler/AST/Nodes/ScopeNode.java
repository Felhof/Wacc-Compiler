package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.backend.NodeVisitor;

public class ScopeNode extends Node {

  private SymbolTable symbolTable;
  private ParentNode parentNode;

  public ScopeNode(ParentNode parentNode,
      SymbolTable symbolTable, int lineNumber) {
    super(lineNumber);
    this.parentNode = parentNode;
    this.symbolTable = symbolTable;
  }

  @Override
  public String toString() {
    return "NewScopeNode:" + parentNode.toString();
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitScopeNode(this);
  }

  public SymbolTable symbolTable() {
    return symbolTable;
  }

  public ParentNode parentNode() {
    return parentNode;
  }

}
