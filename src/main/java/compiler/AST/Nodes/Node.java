package compiler.AST.Nodes;

import compiler.AST.ASTData;
import compiler.visitors.backend.NodeVisitor;

public abstract class Node implements ASTData {
  int lineNumber;

  public Node(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  public abstract void accept(NodeVisitor visitor);

}
