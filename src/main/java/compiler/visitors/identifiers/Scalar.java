package compiler.visitors.identifiers;

public class Scalar extends Type {

  private final int min;
  private final int max;

  public Scalar(int min, int max) {
    this.min = min;
    this.max = max;
  }
}
