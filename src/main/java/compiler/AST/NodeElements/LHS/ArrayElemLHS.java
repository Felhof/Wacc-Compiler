package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.ASTVisitor;

public class ArrayElemLHS extends ArrayElem {

  public ArrayElemLHS(Type type, String varName, Expr[] indexes) {
    super(type, varName, indexes);
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitArrayElemLHS(this);
  }
}
