package compiler.AST.NodeElements;

import compiler.AST.Types.Type;
import compiler.AST.ASTData;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

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

  public abstract REG accept(NodeElemVisitor visitor);

}
