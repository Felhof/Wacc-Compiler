package compiler.visitors.NodeElements;

import compiler.visitors.Returnable;

public class PairType implements Type, Returnable {

  Type fst;
  Type snd;

  public PairType(Type fst, Type snd) {
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public boolean equals(Type type) {
    return type instanceof PairType
        && ((PairType) type).fst.equals(this.fst)
        && ((PairType) type).snd.equals(this.snd);
  }
}
