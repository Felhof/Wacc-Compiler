package compiler.visitors.NodeElements.RHS;

import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.Types.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinExpr extends Expr {

  private final static Type intType = new BasicType(BasicType.TYPE.INT);
  private final static Type charType = new BasicType(BasicType.TYPE.CHAR);
  private final static Type boolType = new BasicType(BasicType.TYPE.BOOL);
  private final static Type stringType = new BasicType(BasicType.TYPE.STRING);

  private final static List<Type> typesInt = Arrays.asList(intType);
  private final static List<Type> typesIntChar =
      Arrays.asList(intType, charType);
  private final static List<Type> typesBool = Arrays.asList(boolType);
  private final static List<Type> typesAny =
      Arrays.asList(intType, charType, boolType, stringType);

  private BINOP operator;
  private Expr lhs;
  private Expr rhs;

  public BinExpr(Expr lhs, BINOP operator, Expr rhs) {
    super(operator.returnType());
    this.operator = operator;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public String isTypeCompatible() {
    if (!contains(operator.argTypes(), lhs.type())) {
      return "Incompatible type " + lhs.type().toString();
    }
    if (!contains(operator.argTypes(), rhs.type())) {
      return "Incompatible type " + rhs.type().toString();
    }
    if (!lhs.type().equals(rhs.type())) {
      return "Incompatible type at " + rhs.toString()
          + " (expected: " + lhs.type().toString()
          + ", actual: " + rhs.type.toString();
    }
    return null;
  }

  private boolean contains(List<Type> argTypes, Type type) {
    return argTypes.stream().filter(t -> t.equals(type)).toArray().length > 0;
  }

  @Override
  public String toString() {
    return "BinExpr(" + lhs.toString() + " " + operator.op().toString() + " " + rhs.toString() +")";
  }

  public enum BINOP {
    MUL("*", typesInt, intType), DIV("/", typesInt, intType),
    MOD("%", typesInt, intType), PLUS("+", typesInt, intType),
    MINUS("-", typesInt, intType), GT(">", typesIntChar, boolType),
    GE(">=", typesIntChar, boolType), LT("<", typesIntChar, boolType),
    LE("<=", typesIntChar, boolType), EQUAL("==", typesAny, boolType),
    NOTEQUAL("!=", typesAny, boolType), AND("&&", typesBool, boolType),
    OR("||", typesBool, boolType);

    private String op;
    private List<Type> argTypes;
    private Type returnType;
    private static Map<String, BINOP> map;

    BINOP(String op,
        List<Type> argTypes,
        Type returnType) {
      this.op = op;
      this.argTypes = argTypes;
      this.returnType = returnType;

    }

    public String op() {
      return op;
    }

    public List<Type> argTypes() {
      return argTypes;
    }

    public Type returnType() { return returnType; }

    static {
      map = new HashMap<>();
      for(BINOP t : BINOP.values()) {
        map.put(t.op(), t);
      }
    }

    public static BINOP get(String string) {
      return map.get(string);
    }
  }
}


