package compiler.AST.NodeElements;

import compiler.AST.Types.Type;
import compiler.visitors.Returnable;

public abstract class NodeElem implements Returnable {
  protected Type type;

  public NodeElem(Type type) {
    this.type = type;
  }

  public Type type() {
    return type;
  }

  @Override
  public String toString() {
    return type.toString();
  }
}
