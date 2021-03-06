package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class IdentRHS extends Ident {

  public IdentRHS(String varName, Type type) {
    super(varName, type);
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitIdentRHS(this);
  }
}
