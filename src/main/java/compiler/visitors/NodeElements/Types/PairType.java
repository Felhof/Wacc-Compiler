package compiler.visitors.NodeElements.Types;

public class PairType extends Type {

  Type fst;
  Type snd;

  public PairType(Type fst, Type snd) {
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public boolean equals(Type type) {
    if (!(type instanceof PairType)) {
      return false;
    }
    PairType other = (PairType) type;
    return ((this.fst instanceof PairType && other.fst instanceof PairType)
          || this.fst.equals(((PairType) type).fst))
        && ((this.snd instanceof PairType && other.snd instanceof PairType)
          || this.snd.equals(((PairType) type).snd));
    }
}
