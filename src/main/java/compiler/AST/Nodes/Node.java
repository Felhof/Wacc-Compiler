package compiler.AST.Nodes;

import compiler.AST.ASTData;

public abstract class Node implements ASTData {
  int lineNumber;

  public Node(int lineNumber) {
    this.lineNumber = lineNumber;
  }
}
