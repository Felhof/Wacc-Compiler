package compiler.AST.Nodes;

import compiler.AST.ASTData;
import compiler.AST.SymbolTable.SymbolTable;
import java.util.List;

public class AST implements ASTData {

  private List<FuncNode> funcNodes;
  private ParentNode root;
  private SymbolTable symbolTable;
  private int stackPointerOffset; // amount of bytes to be stored in the stack

  public AST(List<FuncNode> funcNodes, ParentNode root, SymbolTable symbolTable, int stackPointerOffset) {
    this.funcNodes = funcNodes;
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

  public int stackOffset() {
    return stackPointerOffset;
  }

  public List<FuncNode> funcNodes() {
    return funcNodes;
  }
}
