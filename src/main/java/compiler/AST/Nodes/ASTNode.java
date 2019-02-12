package compiler.AST.Nodes;

import java.util.ArrayList;
import java.util.List;

public class ASTNode implements Node {

  private List<Node> children;

  public ASTNode() {
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
}
