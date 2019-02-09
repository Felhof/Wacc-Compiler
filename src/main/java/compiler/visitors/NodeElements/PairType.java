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
    // If a pair has generic types it matches any other type that is a pair
    if (type instanceof PairType
        && this.fst.equals(new BasicType(TYPE.RECOVERY))
        && this.snd.equals(new BasicType(TYPE.RECOVERY))) {
      return true;
    } else {
      return type instanceof PairType
          && this.fst.equals(((PairType) type).fst)
          && this.snd.equals(((PairType) type).snd);
    }
  }
}
