package compiler.AST;

import compiler.IR.Operand.REG;
import compiler.visitors.ASTVisitor;

public interface ASTData {

  default REG accept(ASTVisitor visitor) {
    return null;
  }

}
