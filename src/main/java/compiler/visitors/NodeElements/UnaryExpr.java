package compiler.visitors.NodeElements;

import java.util.HashMap;
import java.util.Map;

public class UnaryExpr extends Expr {

  private final static Type intType = new BasicType(TYPE.INT);
  private final static Type charType = new BasicType(TYPE.CHAR);
  private final static Type boolType = new BasicType(TYPE.BOOL);

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



  public enum UNOP {
    NEG("!", boolType, boolType), PLUS("+", intType, intType),
    MINUS("-", intType, intType), ORD("ord", charType, intType),
    CHR("chr", intType, charType);
    // LEN("len", arrayType, intType); // implement array type

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


}
