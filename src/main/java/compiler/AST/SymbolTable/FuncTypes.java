package compiler.AST.SymbolTable;

import compiler.AST.Types.Type;
import compiler.visitors.ASTData;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;
import java.util.List;

public class FuncTypes implements ASTData {
  private List<Type> paramTypeList;
  private Type returnType;

  public FuncTypes(List<Type> paramTypeList,
      Type returnType) {
    this.paramTypeList = paramTypeList;
    this.returnType = returnType;
  }

  public List<Type> getParamTypes() {
    return paramTypeList;
  }

  public Type getReturnType() {
    return returnType;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return null;
  }
}
