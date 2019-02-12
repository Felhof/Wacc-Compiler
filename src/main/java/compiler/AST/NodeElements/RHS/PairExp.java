package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.Type;

public class PairExp extends Expr {

  public PairExp(Type type) {
    super(type);
  }

  @Override
  public String toString() {
    return "PairLiter: null";
  }
}
