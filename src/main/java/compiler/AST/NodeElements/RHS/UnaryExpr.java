package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.GenericType;
import compiler.AST.Types.IntType;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;
import java.util.HashMap;
import java.util.Map;

public class UnaryExpr extends Expr {

  private final static Type intType = IntType.getInstance();
  private final static Type charType = CharType.getInstance();
  private final static Type boolType = BoolType.getInstance();
  private final static Type arrayType = GenericType.getInstance();

  private UNOP operator;
  private Expr expr;

  public UnaryExpr(UNOP operator, Expr expr) {
    super(operator.returnType());
    this.operator = operator;
    this.expr = expr;
  }

  public String isTypeCompatible() {
    if (!operator.argType.equals(expr.type())) {
      return " (expected: " + operator.argType.toString()
          + ", actual: " + expr.type().toString() + ")";
    }
    return null;
  }

  public UNOP operator() {
    return operator;
  }

  public Expr insideExpr() {
    return expr;
  }

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitUnaryExpr(this);
  }

  @Override
  public int sizeOf() {
    //TODO: need to think about this
    return 0;
  }

  public enum UNOP {
    NEG("!", boolType, boolType), PLUS("+", intType, intType),
    MINUS("-", intType, intType), ORD("ord", charType, intType),
    CHR("chr", intType, charType), LEN("len", arrayType, intType);

    private String op;
    private Type argType;
    private Type returnType;
    private static Map<String, UNOP> map;

    UNOP(String op, Type argType,
        Type returnType) {
      this.op = op;
      this.argType = argType;
      this.returnType = returnType;
    }

    public String op() {
      return op;
    }

    public Type argType() {
      return argType;
    }

    public Type returnType() { return returnType; }

    static {
      map = new HashMap<>();
      for(UNOP t : UNOP.values()) {
        map.put(t.op(), t);
      }
    }

    public static UNOP get(String string) {
      return map.get(string);
    }
  }

  @Override
  public String toString() {
    return "UnaryExpr:" + operator.toString() + expr.toString();
  }
}
