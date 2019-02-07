package compiler.visitors;

import java.util.ArrayList;
import java.util.List;

public class ASTNode implements Returnable {

  private List<ASTNode> children;

  public ASTNode() {
    children = new ArrayList<>();
  }

  public void add(ASTNode child) {
    children.add(child);
  }

}
