package compiler.visitors.NodeElements;

import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.Returnable;
import java.util.ArrayList;
import java.util.List;

public class ListExpr implements Returnable {
  private List<Expr> exprList;

  public ListExpr() {
    this.exprList = new ArrayList<>();
  }

  public void add(Expr expr) {
    exprList.add(expr);
  }

  public boolean hasSameTypes(ListExpr list) {
    if (exprList.size() != list.exprList().size()) {
      return false;
    } else {
      for (int i = 0; i < exprList.size(); i++) {
        if (!exprList.get(i).type().equals(list.exprList().get(i).type())) {
          return false;
        }
      }
      return true;
    }
  }

  public List<Expr> exprList() {
    return exprList;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("TypeList = ");
    exprList.forEach(e -> sb.append(e.toString()).append(" "));
    return sb.toString();
  }
}
