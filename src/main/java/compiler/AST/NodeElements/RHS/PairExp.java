package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.Type;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class PairExp extends Expr {

  public PairExp(Type type) {
    super(type);
  }

  @Override
  public String toString() {
    return "PairLiter: null";
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitNullPair();
  }
}
