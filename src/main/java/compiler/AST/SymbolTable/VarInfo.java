package compiler.AST.SymbolTable;

import compiler.AST.ASTData;
import compiler.AST.Types.Type;

public class VarInfo implements ASTData {

  private Type type;
  private Integer localOffset;

  public VarInfo(Type type, Integer stackOffset) {
    this.type = type;
    this.localOffset = stackOffset;
  }

  public Type getType() {
    return type;
  }

  public Integer getLocalOffset() {
    return localOffset;
  }

  public void setLocalOffset(Integer stackOffset) {
    this.localOffset = stackOffset;
  }
}
