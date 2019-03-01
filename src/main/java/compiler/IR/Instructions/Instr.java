package compiler.IR.Instructions;

import static compiler.IR.IR.currentId;

public abstract class Instr {

  private Integer id = currentId;
  public abstract String toString();

}
