package compiler.AST.NodeElements;

import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.Types.Type;
import compiler.visitors.ASTData;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListExpr implements ASTData {

  private List<String> paramNames;
  private List<Expr> exprList;
  private boolean isParams;
  private int bytesPushed;


  public ListExpr(boolean isParams) {
    this.isParams = isParams;
    this.exprList = new ArrayList<>();
    this.paramNames = new ArrayList<>();
    bytesPushed = 0;
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

  public int bytesPushed() {
    return bytesPushed;
  }

  public List<Expr> exprList() {
    return exprList;
  }

  public void addExpr(Expr expr) {
    exprList.add(expr);
  }

  public void addBytes(int bytes) {
    bytesPushed += bytes;
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

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    if (isParams) {
      visitor.visitParams(this);
    } else {
      visitor.visitArgs(this);
    }
    return null;
  }

  public void addParamName(String text) {
    paramNames.add(text);
  }

  public List<String> paramNames() {
    return paramNames;
  }
}
