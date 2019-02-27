package compiler.AST.NodeElements;

import compiler.AST.Types.Type;
import compiler.AST.ASTData;

public abstract class NodeElem implements ASTData {
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
