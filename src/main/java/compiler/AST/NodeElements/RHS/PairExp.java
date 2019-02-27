package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class PairExp extends Expr {

  public PairExp(Type type) {
    super(type);
  }

  @Override
  public String toString() {
    return "PairLiter: null";
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitNullPair();
  }
}
