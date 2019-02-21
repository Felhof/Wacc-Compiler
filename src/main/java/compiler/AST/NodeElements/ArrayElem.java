package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class ArrayElem extends Expr implements LHS{

  private String varName;
  private Expr[] indexes;

  public ArrayElem(Type type, String varName, Expr[] indexes) {
    super(type);
    this.varName = varName;
    this.indexes = indexes;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }

  @Override
  public String varName() {
    return varName;
  }
}
