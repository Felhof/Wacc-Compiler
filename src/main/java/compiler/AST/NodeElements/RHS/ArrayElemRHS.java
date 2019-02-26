package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class ArrayElemRHS extends ArrayElem {

  public ArrayElemRHS(Type type, String varName,
      Expr[] indexes) {
    super(type, varName, indexes);
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitArrayElemRHS(this);
  }
}
