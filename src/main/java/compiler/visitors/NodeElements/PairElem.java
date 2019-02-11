package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.Types.Type;

public class PairElem extends NodeElem {
  private int posInPair;

  public PairElem(Type type, int posInPair) {
    super(type);
    this.posInPair = posInPair;
  }
}
