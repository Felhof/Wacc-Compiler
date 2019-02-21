package compiler.AST.SymbolTable;

import compiler.AST.Types.Type;
import compiler.visitors.ASTData;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class VarInfo implements ASTData {

  private Type type;
  private Integer stackOffset;

  public VarInfo(Type type, Integer stackOffset) {
    this.type = type;
    this.stackOffset = stackOffset;
  }

  public Type getType() {
    return type;
  }

  public Integer getStackOffset() {
    return stackOffset;
  }

  public void setStackOffset(Integer stackOffset) {
    this.stackOffset = stackOffset;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
