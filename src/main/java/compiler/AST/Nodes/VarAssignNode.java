package compiler.AST.Nodes;

import compiler.AST.NodeElements.NodeElem;
import compiler.IR.Operand.REG;
import compiler.visitors.ASTVisitor;

public class VarAssignNode extends Node {

  private NodeElem lhs;
  private NodeElem rhs;

  public VarAssignNode(NodeElem lhs,
      NodeElem rhs, int lineNumber) {
    super(lineNumber);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarAssignment" + lhs.toString()
        + "= " + rhs.toString();
  }

  @Override
  public REG accept(ASTVisitor visitor) {
    return visitor.visitAssignNode(this);
  }

  public NodeElem rhs() {
    return rhs;
  }

  public NodeElem lhs() {
    return lhs;
  }
}
