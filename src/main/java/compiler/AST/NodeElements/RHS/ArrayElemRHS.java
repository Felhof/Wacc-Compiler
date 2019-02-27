package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.ASTVisitor;

public class ArrayElemRHS extends ArrayElem {

  public ArrayElemRHS(Type type, String varName,
      Expr[] indexes) {
    super(type, varName, indexes);
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitArrayElemRHS(this);
  }
}
