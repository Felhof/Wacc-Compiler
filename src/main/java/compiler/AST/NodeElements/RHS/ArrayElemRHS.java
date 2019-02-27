package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class ArrayElemRHS extends ArrayElem {

  public ArrayElemRHS(Type type, String varName,
      Expr[] indexes) {
    super(type, varName, indexes);
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitArrayElemRHS(this);
  }
}
