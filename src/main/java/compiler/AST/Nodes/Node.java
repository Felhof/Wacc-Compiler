package compiler.AST.Nodes;

public abstract class Node {
  int lineNumber;

  public Node(int lineNumber) {
    this.lineNumber = lineNumber;
  }
}
