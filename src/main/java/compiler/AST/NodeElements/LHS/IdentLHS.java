package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class IdentLHS extends Ident {

  public IdentLHS(String varName, Type type) {
    super(varName, type);
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitIdentLHS(this);
  }
}
