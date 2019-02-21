package compiler.AST.NodeElements.RHS;

import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.IntType;
import compiler.AST.Types.Type;
import compiler.visitors.ASTVisitor;
import compiler.visitors.CodeGenData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinExpr extends Expr {

  private final static Type intType = IntType.getInstance();
  private final static Type charType = CharType.getInstance();
  private final static Type boolType = BoolType.getInstance();

  private final static List<Type> typesInt = Arrays.asList(intType);
  private final static List<Type> typesIntChar =
      Arrays.asList(intType, charType);
  private final static List<Type> typesBool = Arrays.asList(boolType);

  private BINOP operator;
  private Expr lhs;
  private Expr rhs;

  public BinExpr(Expr lhs, BINOP operator, Expr rhs) {
    super(operator.returnType());
    this.operator = operator;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Expr lhs(){ return lhs; }
  public Expr rhs() { return rhs; }

  public String isTypeCompatible() {
    if (!operator.op.equals("==") && !operator.op().equals("!="))  {
      String tempReturn = "Binary Operator " + operator.op
          + " cannot take as its ";
      if (!contains(operator.argTypes(), lhs.type())) {
        return tempReturn + "LHS the type " + lhs.type().toString();
      }
      if (!contains(operator.argTypes(), rhs.type())) {
        return tempReturn + "RHS the type " + rhs.type().toString();
      }
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

  @Override
  public CodeGenData accept(ASTVisitor visitor) {
    return visitor.visitBinaryExp(this);
  }

  @Override
  public int sizeOf() {
    //TODO: need to think about this
    return 0;
  }

  public enum BINOP {
    MUL("*", typesInt, intType), DIV("/", typesInt, intType),
    MOD("%", typesInt, intType), PLUS("+", typesInt, intType),
    MINUS("-", typesInt, intType), GT(">", typesIntChar, boolType),
    GE(">=", typesIntChar, boolType), LT("<", typesIntChar, boolType),
    LE("<=", typesIntChar, boolType), EQUAL("==", null, boolType),
    NOTEQUAL("!=", null, boolType), AND("&&", typesBool, boolType),
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


