package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.ArrType;
import compiler.AST.Types.BasicType;
import compiler.AST.Types.Type;
import java.util.HashMap;
import java.util.Map;

public class UnaryExpr extends Expr {

  private final static Type intType = new BasicType(BasicType.TYPE.INT);
  private final static Type charType = new BasicType(BasicType.TYPE.CHAR);
  private final static Type boolType = new BasicType(BasicType.TYPE.BOOL);
  private final static Type arrayType = new Type() {
    @Override
    public boolean equals(Type type) {
      return type instanceof ArrType;
    }

    @Override
    public String toString() {
      return "T[]";
    }
  };

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
