package compiler.visitors.NodeElements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinExpr extends Expr {

  private final static List<TYPE> typesInt = Arrays.asList(TYPE.INT);
  private final static List<TYPE> typesIntChar = Arrays.asList(TYPE.INT, TYPE.CHAR);
  private final static List<TYPE> typesBool = Arrays.asList(TYPE.BOOL);
  private final static List<TYPE> typesAny = Arrays.asList(TYPE.values());

  private BINOP operator;
  private Expr lhs;
  private Expr rhs;

  public BinExpr(Expr lhs, BINOP operator, Expr rhs) {
    super(new BasicType(operator.returnType()));
    this.operator = operator;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public boolean isTypeCompatible() {
    return lhs.type() == rhs.type() && operator.argTypes().contains(lhs.type());
  }

  @Override
  public void setType() {
  }

  public enum BINOP {
    MUL("*", typesInt, TYPE.INT), DIV("/", typesInt, TYPE.INT),
    MOD("%", typesInt, TYPE.INT), PLUS("+", typesInt, TYPE.INT),
    MINUS("-", typesInt, TYPE.INT), GT(">", typesIntChar, TYPE.BOOL),
    GE(">=", typesIntChar, TYPE.BOOL), LT("<", typesIntChar, TYPE.BOOL),
    LE("<=", typesIntChar, TYPE.BOOL), EQUAL("==", typesAny, TYPE.BOOL),
    NOTEQUAL("!=", typesAny, TYPE.BOOL), AND("&&", typesBool, TYPE.BOOL),
    OR("||", typesBool, TYPE.BOOL);

    private String op;
    private List<TYPE> argTypes;
    private TYPE returnType;
    private static Map<String, BINOP> map;

    BINOP(String op,
        List<TYPE> argTypes,
        TYPE returnType) {
      this.op = op;
      this.argTypes = argTypes;
      this.returnType = returnType;

    }

    public String op() {
      return op;
    }

    public List<TYPE> argTypes() {
      return argTypes;
    }

    public TYPE returnType() { return returnType; }

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


