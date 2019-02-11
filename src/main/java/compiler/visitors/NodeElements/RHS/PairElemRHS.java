package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.Type;

public class PairElemRHS extends AssignRHS {
  private int posInPair;

  public PairElemRHS(Type type, int posInPair) {
    super(type);
    this.posInPair = posInPair;
  }
}
