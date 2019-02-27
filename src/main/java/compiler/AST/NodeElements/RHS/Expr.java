package compiler.AST.NodeElements.RHS;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.instr.Operand.Operand;

public abstract class Expr extends NodeElem {
  public Expr(Type type) {
    super(type);
  }
}
