package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class IdentLHS extends Ident implements LHS {

  public IdentLHS(String varName, Type type) {
    super(varName, type);
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitIdentLHS(this);
  }
}
