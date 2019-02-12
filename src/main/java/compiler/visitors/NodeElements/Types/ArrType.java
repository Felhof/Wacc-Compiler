package compiler.visitors.NodeElements.Types;

public class ArrType extends Type {

  private Type elemType;
  private int dimension;

  public ArrType(Type elemType) {
    this.elemType = elemType;
    this.dimension = 1;
  }

  public ArrType(Type elemType, int dimension) {
    this.elemType = elemType;
    this.dimension = dimension;
  }

  public Type elemType() {
    return elemType;
  }

  public int dimension() {
    return dimension;
  }

  public ArrType addDimension() {
    this.dimension++;
    return this;
  }



  @Override
  public boolean equals(Type type) {
    return type instanceof ArrType
        && this.elemType.equals(((ArrType) type).elemType())
        && this.dimension == ((ArrType) type).dimension();
  }

  @Override
  public String toString() {
    return elemType.toString() + bracketsString(dimension);
  }

  public static String bracketsString(int dim) {
    return (dim > 0) ? ("[]" + bracketsString(dim - 1)) : "";
  }

  public static Type getArrayType(Type elemType) {
    return (elemType instanceof ArrType) ?
        ((ArrType) elemType).addDimension() : new ArrType(elemType);
  }

  public Type getArrayElem(int indexes) {
    return (indexes == this.dimension) ? elemType() :
        new ArrType(this.elemType, dimension - indexes);
  }

}
