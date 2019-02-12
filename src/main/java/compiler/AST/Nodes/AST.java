package compiler.AST.Nodes;

import compiler.AST.SymbolTable.SymbolTable;
import compiler.visitors.Returnable;

public class AST implements Returnable {
  private ASTNode root;
  private SymbolTable symbolTable;

  public AST(ASTNode root, SymbolTable symbolTable) {
    this.root = root;
    this.symbolTable = symbolTable;
  }

}
