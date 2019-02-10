package compiler.visitors.NodeElements.LHS;

import compiler.visitors.NodeElements.LHS.AssignLHS;
import compiler.visitors.NodeElements.Types.Type;

public class IdentLHS extends AssignLHS {

  public IdentLHS(Type type) {
    super(type);
  }
}
