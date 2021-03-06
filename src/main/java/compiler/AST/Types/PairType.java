package compiler.AST.Types;

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
    if (other.fst == GenericType.getInstance()
        && other.snd == GenericType.getInstance()) {
      return true;
    }

    return ((this.fst instanceof PairType && other.fst instanceof PairType)
          || this.fst.equals(((PairType) type).fst))
        && ((this.snd instanceof PairType && other.snd instanceof PairType)
          || this.snd.equals(((PairType) type).snd));
  }

  @Override
  public String toString() {
    return "PAIR(" + fst.toString() + "," + snd.toString() + ")";
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }

}
