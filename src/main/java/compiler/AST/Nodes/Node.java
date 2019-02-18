package compiler.AST.Nodes;

import compiler.visitors.ASTData;

public abstract class Node implements ASTData {
  int lineNumber;

  public Node(int lineNumber) {
    this.lineNumber = lineNumber;
  }
}
