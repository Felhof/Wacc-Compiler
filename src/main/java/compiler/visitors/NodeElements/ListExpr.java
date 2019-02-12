package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.Returnable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListExpr implements Returnable {

  private List<Expr> exprList;

  public ListExpr() {
    this.exprList = new ArrayList<>();
  }

  public static boolean hasSameTypes(List<Type> typeList1,
      List<Type> typeList2) {
    if (typeList1.size() != typeList2.size()) {
      return false;
    } else {
      for (int i = 0; i < typeList1.size(); i++) {
        if (!typeList1.get(i).equals(typeList2.get(i))) {
          return false;
        }
      }
      return true;
    }
  }

  public void add(Expr expr) {
    exprList.add(expr);
  }

  public List<Type> getExprTypes() {
    return exprList.stream().map(NodeElem::type).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("TypeList = ");
    exprList.forEach(e -> sb.append(e.toString()).append(" "));
    return sb.toString();
  }
}
