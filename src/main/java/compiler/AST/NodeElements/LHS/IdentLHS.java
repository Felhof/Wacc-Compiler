package compiler.AST.NodeElements.LHS;

import compiler.AST.NodeElements.Ident;
import compiler.AST.Types.Type;
import compiler.IR.Operand.REG;
import compiler.visitors.backend.NodeElemVisitor;

public class IdentLHS extends Ident {

  public IdentLHS(String varName, Type type) {
    super(varName, type);
  }

  @Override
  public REG accept(NodeElemVisitor visitor) {
    return visitor.visitIdentLHS(this);
  }
}
