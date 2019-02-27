package compiler.visitors;

import compiler.instr.REG;

public interface ASTData {

  default REG accept(ASTVisitor visitor) {
    return null;
  }

}
