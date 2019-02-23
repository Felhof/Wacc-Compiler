package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;

public class Ident extends LHS {

  private String varName;

  public Ident(String varName, Type type) {
    super(type);
    this.varName = varName;
  }

  @Override
  public String toString() {
    return "Ident: " +
        varName;
  }

  public String varName() {
    return varName;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitIdent(this);
  }

  @Override
  public int sizeOf() {
    //TODO: get type from symbol or augment the map in visitor
    return (type instanceof CharType || type instanceof BoolType ? 1 : 4);
  }
}
