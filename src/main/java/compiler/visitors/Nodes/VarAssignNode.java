package compiler.visitors.Nodes;

import compiler.visitors.NodeElements.LHS.AssignLHS;
import compiler.visitors.NodeElements.RHS.AssignRHS;

public class VarAssignNode implements Node {

  private AssignLHS lhs;
  private AssignRHS rhs;

  public VarAssignNode(AssignLHS lhs,
      AssignRHS rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "VarAssignment" + lhs.toString()
        + "= " + rhs.toString();
  }
}
