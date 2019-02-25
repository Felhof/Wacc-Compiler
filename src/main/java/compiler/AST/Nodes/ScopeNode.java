package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class ScopeNode extends Node {

  private SymbolTable symbolTable;
  private int stackOffset;
  private ParentNode parentNode;

  public ScopeNode(ParentNode parentNode,
      SymbolTable symbolTable, int lineNumber, int stackOffset) {
    super(lineNumber);
    this.parentNode = parentNode;
    this.symbolTable = symbolTable;
    this.stackOffset = stackOffset;
  }

  @Override
  public String toString() {
    return "NewScopeNode:" + parentNode.toString();
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitNewScope(this);
  }

  public SymbolTable symbolTable() {
    return symbolTable;
  }

  public ParentNode parentNode() {
    return parentNode;
  }

  public int stackOffset() {
    return stackOffset;
  }
}
