package compiler.AST.Nodes;

import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;
import java.util.ArrayList;
import java.util.List;

public class ParentNode extends Node {

  private List<Node> children;

  public ParentNode(int lineNumber) {
    super(lineNumber);
    children = new ArrayList<>();
  }

  public void add(Node child) {
    children.add(child);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(Node n : children) {
      sb.append(n.toString());
      sb.append("\n");
    }
    return sb.toString();
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }

  public List<Node> children() {
    return children;
  }
}
