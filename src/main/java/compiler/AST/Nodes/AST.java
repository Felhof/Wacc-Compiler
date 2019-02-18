package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.ASTData;

public class AST implements ASTData {
  private ParentNode root;
  private SymbolTable symbolTable;

  public AST(ParentNode root, SymbolTable symbolTable) {
    this.root = root;
    this.symbolTable = symbolTable;
  }

  public ParentNode root() {
    return root;
  }

  public SymbolTable symbolTable() {
    return symbolTable;
  }
}
