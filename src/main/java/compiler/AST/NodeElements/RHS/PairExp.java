package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class PairExp extends Expr {

  public PairExp(Type type) {
    super(type);
  }

  @Override
  public int sizeOf() {
    //TODO: not sure
    return 4;
  }

  @Override
  public String toString() {
    return "PairLiter: null";
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
