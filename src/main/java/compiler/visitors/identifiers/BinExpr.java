package compiler.visitors.identifiers;

public class BinExpr extends Expr {
  private BINOP operator;
  private Expr lhs;
  private Expr rhs;

  public BinExpr(Expr lhs, String operator, Expr rhs) {
    this.operator = BINOP.valueOf(operator);
    this.lhs = lhs;
    this.rhs = rhs;
    super.type = this.operator.type();
  }

  enum BINOP {
    MUL("*", TYPE.INT), DIV("/", TYPE.INT), MOD("%", TYPE.INT), PLUS("+", TYPE.INT),
    MINUS("-", TYPE.INT), GT(">", TYPE.BOOL), GE(">=", TYPE.BOOL), LT("<", TYPE.BOOL),
    LE("<=", TYPE.BOOL), EQUAL("==", TYPE.BOOL), NOTEQUAL("!=", TYPE.BOOL),
    AND("&&", TYPE.BOOL), OR("||", TYPE.BOOL);

    private String op;
    private TYPE type;

    BINOP(String op, TYPE type) {
      this.op = op;
      this.type = type;
    }

    public String op() {
      return op;
    }

    public TYPE type() { return type; }
  }
}


