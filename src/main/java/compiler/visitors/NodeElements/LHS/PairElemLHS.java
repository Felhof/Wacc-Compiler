package compiler.visitors.NodeElements.LHS;

import compiler.visitors.NodeElements.Types.Type;

public class PairElemLHS extends AssignLHS {
  private int posInPair;

  public PairElemLHS(Type type, int posInPair) {
    super(type);
    this.posInPair = posInPair;
  }
}
