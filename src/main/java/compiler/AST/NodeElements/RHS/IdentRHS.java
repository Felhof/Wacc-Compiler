package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class IdentRHS extends Ident {

  public IdentRHS(String varName, Type type) {
    super(varName, type);
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitIdentRHS(this);
  }
}
