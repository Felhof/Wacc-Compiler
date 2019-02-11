package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.Type;

public class PairElem extends AssignRHS {
  private int posInPair;

  public PairElem(Type type, int posInPair) {
    super(type);
    this.posInPair = posInPair;
  }
}
