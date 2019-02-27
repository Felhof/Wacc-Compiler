package compiler.AST.Nodes;

import compiler.AST.ASTData;
import compiler.AST.SymbolTable.SymbolTable;

public class AST implements ASTData {
  private ParentNode root;
  private SymbolTable symbolTable;
  private int stackPointerOffset;

  public AST(ParentNode root, SymbolTable symbolTable, int stackPointerOffset) {
    this.root = root;
    this.symbolTable = symbolTable;
    this.stackPointerOffset = stackPointerOffset;
  }

  public ParentNode root() {
    return root;
  }

  public SymbolTable symbolTable() {
    return symbolTable;
  }

  public String stackOffset() {
    return String.valueOf(stackPointerOffset);
  }
}
