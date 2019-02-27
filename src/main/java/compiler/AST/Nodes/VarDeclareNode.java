package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Types.Type;
import compiler.instr.REG;
import compiler.visitors.ASTVisitor;

public class VarDeclareNode extends Node {
  private Type varType;
  private String varName;
  private NodeElem rhs;

  public VarDeclareNode(Type varType, String varName,
      NodeElem rhs, int lineNumber) {
    super(lineNumber);
    this.varType = varType;
    this.varName = varName;
    this.rhs = rhs;
  }

  public Type varType(){ return varType; }
  public NodeElem rhs(){ return rhs; }

  public String varName() {
    return varName;
  }

  @Override
  public String toString() {
    return "VarDeclaration: " + varType.toString() + " "
        + varName + " = " + rhs.toString();
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitVarDeclareNode(this);
  }
}
