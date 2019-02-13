package compiler.visitors;

import antlr.BasicParser.Arg_listContext;
import antlr.BasicParser.ArrayElemLhsContext;
import antlr.BasicParser.ArrayExpContext;
import antlr.BasicParser.ArrayTypeContext;
import antlr.BasicParser.Array_elemContext;
import antlr.BasicParser.Array_literContext;
import antlr.BasicParser.AssignArrayContext;
import antlr.BasicParser.AssignStatContext;
import antlr.BasicParser.BaseTypeContext;
import antlr.BasicParser;
import antlr.BasicParser.BinaryExpContext;
import antlr.BasicParser.BoolExpContext;
import antlr.BasicParser.BracketExpContext;
import antlr.BasicParser.CharExpContext;
import antlr.BasicParser.ExitStatContext;
import antlr.BasicParser.ExprContext;
import antlr.BasicParser.FuncCallContext;
import antlr.BasicParser.FuncContext;
import antlr.BasicParser.IdentExpContext;
import antlr.BasicParser.IdentLhsContext;
import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.IntExpContext;
import antlr.BasicParser.NewPairContext;
import antlr.BasicParser.NewScopeStatContext;
import antlr.BasicParser.PairElemArrayTypeContext;
import antlr.BasicParser.PairElemBaseTypeContext;
import antlr.BasicParser.PairElemPairTypeContext;
import antlr.BasicParser.PairExpContext;
import antlr.BasicParser.PairTypeContext;
import antlr.BasicParser.Pair_elemContext;
import antlr.BasicParser.ParamContext;
import antlr.BasicParser.Param_listContext;
import antlr.BasicParser.Pair_typeContext;
import antlr.BasicParser.PrintStatContext;
import antlr.BasicParser.PrintlnStatContext;
import antlr.BasicParser.ProgContext;
import antlr.BasicParser.ReadStatContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.ReturnStatContext;
import antlr.BasicParser.StatContext;
import antlr.BasicParser.StrExpContext;
import antlr.BasicParser.StringTypeContext;
import antlr.BasicParser.UnaryExpContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParser.WhileStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.Node;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.FreeNode;
import compiler.AST.Nodes.FuncNode;
import compiler.AST.Nodes.IfElseNode;
import compiler.AST.Nodes.PrintNode;
import compiler.AST.Nodes.ReturnNode;
import compiler.AST.Nodes.ScopeNode;
import compiler.AST.Nodes.VarAssignNode;
import compiler.AST.Nodes.VarDeclareNode;
import compiler.AST.SymbolTable.FuncTypes;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.NodeElements.ArrayElem;
import compiler.AST.NodeElements.Ident;
import compiler.AST.NodeElements.ListExpr;
import compiler.AST.NodeElements.RHS.ArrayLiter;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.NodeElements.RHS.FuncCall;
import compiler.AST.NodeElements.PairElem;
import compiler.AST.NodeElements.RHS.PairExp;
import compiler.AST.Types.ArrType;
import compiler.AST.Types.BasicType;
import compiler.AST.NodeElements.RHS.Pair;
import compiler.AST.Types.BasicType.TYPE;
import compiler.AST.Types.GenericType;
import compiler.AST.Types.PairType;
import compiler.AST.Types.Type;
import compiler.AST.NodeElements.RHS.UnaryExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.NodeElements.RHS.BinExpr;
import compiler.AST.NodeElements.RHS.BinExpr.BINOP;
import compiler.AST.NodeElements.RHS.BoolExpr;
import compiler.AST.NodeElements.RHS.CharExpr;
import compiler.AST.NodeElements.RHS.Expr;
import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.NodeElements.RHS.StringExpr;
import compiler.AST.Nodes.WhileNode;
import compiler.AST.Nodes.ReadNode;
import java.util.List;

public class SemanticVisitor extends BasicParserBaseVisitor<Returnable> {

  private BasicParser parser;
  private SymbolTable currentST;
  private ParentNode currentParentNode;

  public SemanticVisitor(BasicParser parser) {
    this.parser = parser;
    currentST = new SymbolTable(null);
  }

  @Override
  public Returnable visitProg(ProgContext ctx) {
    currentParentNode = new ParentNode();
    addFuncDefToST(ctx);
    ctx.func().forEach(f -> currentParentNode.add((Node) visit(f)));
    visit(ctx.stat());
    return new AST(currentParentNode, currentST);
  }

  private void addFuncDefToST(ProgContext ctx) {
    for (int i = 0; i < ctx.func().size(); i++) {
      String funcName = ctx.func(i).IDENT().toString();
      if (currentST.lookUpAllFunc(funcName) != null) {
        parser.notifyErrorListeners(ctx.func(i).start,
            identifierAlreadyDefinedMsg(annotateFunc(funcName)), null);
        continue;
      }
      Type type = (Type) visit(ctx.func(i).type());
      Param_listContext param_listContext = ctx.func(i).param_list();
      ListExpr params = new ListExpr();
      if (param_listContext != null) {
        params = (ListExpr) visit(ctx.func(i).param_list());
      }
      currentST.addFunc(funcName, new FuncTypes(params.getExprTypes(), type));
    }
  }

  @Override
  public Returnable visitFunc(FuncContext ctx) {
    Type funcReturnType = (Type) visit(ctx.type());

    ScopeData funcStat = visitFuncStatInNewScope(ctx.IDENT().getText(),
        ctx.stat(), ctx.param_list(), funcReturnType);

    return new FuncNode(funcReturnType,
        ctx.IDENT().getText(),
        funcStat.paramList(), funcStat.astNode(),
        funcStat.symbolTable());
  }

  @Override
  public Returnable visitParam_list(Param_listContext ctx) {
    ListExpr paramList = new ListExpr();
    ctx.param().forEach(p -> paramList.add(new Ident(p.IDENT().getText(), (Type) visit(p))));
    return paramList;
  }

  @Override
  public Returnable visitParam(ParamContext ctx) {
    Type paramType = (Type) visit(ctx.type());
    if (currentST.getEncSymTable() != null) {
      // don't add parameters to main symbol table
      currentST.addVar(ctx.IDENT().getText(), paramType);
    }
    return paramType;
  }

  @Override
  public Returnable visitRecursiveStat(RecursiveStatContext ctx) {
    visit(ctx.stat(0));
    visit(ctx.stat(1));
    return null;
  }

  @Override
  public Returnable visitPrintStat(PrintStatContext ctx) {
    currentParentNode.add(new PrintNode(false, (Expr) visit(ctx.expr())));
    return null;
  }

  @Override
  public Returnable visitPrintlnStat(PrintlnStatContext ctx) {
    currentParentNode.add(new PrintNode(true, (Expr) visit(ctx.expr())));
    return null;
  }

  @Override
  public Returnable visitReadStat(ReadStatContext ctx) {
    NodeElem lhs = (NodeElem) visit(ctx.assign_lhs());
    if (!isReadableType(lhs)) {
      parser.notifyErrorListeners(ctx.assign_lhs().start,
          incompatibleReadType(ctx.assign_lhs().getText(), lhs.type()),null);
    }
    currentParentNode.add(new ReadNode(lhs));
    return null;
  }

  @Override
  public Returnable visitIfStat(IfStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx.expr(), condition);

    ScopeData thenStat = visitStatInNewScope(ctx.stat(0));
    ScopeData elseStat = visitStatInNewScope(ctx.stat(1));

    currentParentNode.add(new IfElseNode(condition, thenStat.astNode(),
        thenStat.symbolTable(), elseStat.astNode(), elseStat.symbolTable()));
    return null;
  }

  @Override
  public Returnable visitWhileStat(WhileStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx.expr(), condition);

    ScopeData stat = visitStatInNewScope(ctx.stat());

    currentParentNode
        .add(new WhileNode(condition, stat.astNode(), stat.symbolTable()));
    return null;
  }

  @Override
  public Returnable visitVarDeclarationStat(VarDeclarationStatContext ctx) {
    String varName = ctx.IDENT().getText();

    Type varType = (Type) visit(ctx.type());
    Type varTypeDef = currentST.lookUpVarScope(varName);
    NodeElem rhs = (NodeElem) visit(ctx.assign_rhs()); // simple case

    if (!isAssignSameType(varType, rhs)) {
      parser.notifyErrorListeners(ctx.assign_rhs().start,
          incompatibleTypesMsg(ctx.assign_rhs().getText(), varType,
              rhs.type()), null);
    }

    if (varTypeDef != null) {
      parser
          .notifyErrorListeners(ctx.IDENT().getSymbol(),
              identifierAlreadyDefinedMsg(annotateVar(varName)), null);
    } else {
      currentST.addVar(varName, varType);
      currentParentNode.add(new VarDeclareNode(varType, varName, rhs));
    }
    return null;
  }

  @Override
  public Returnable visitBoolExp(BoolExpContext ctx) {
    return new BoolExpr(ctx.bool_liter().getText());
  }

  @Override
  public Returnable visitIntExp(IntExpContext ctx) {
    return new IntExpr(ctx.INTEGER().getText());
  }

  @Override
  public Returnable visitCharExp(CharExpContext ctx) {
    return new CharExpr(ctx.char_liter().getText());
  }

  @Override
  public Returnable visitStrExp(StrExpContext ctx) {
    return new StringExpr(ctx.str_liter().getText());
  }

  @Override
  public Returnable visitBinaryExp(BinaryExpContext ctx) {
    Expr lhs = (Expr) visit(ctx.expr(0));
    Expr rhs = (Expr) visit(ctx.expr(1));

    String op = ctx.getChild(1).getText();

    BinExpr binExpr = new BinExpr(lhs, BINOP.get(op),
        rhs);
    String errorMessage = binExpr.isTypeCompatible();
    if (errorMessage != null) {
      parser.notifyErrorListeners(ctx.start, errorMessage, null);
    }
    else if (!lhs.type().equals(rhs.type())) {
      parser.notifyErrorListeners(ctx.expr(1).start,
          incompatibleTypesMsg(ctx.expr(1).getText(), lhs.type(), rhs.type())
          , null);
    }
    return binExpr;
  }

  @Override
  public Returnable visitUnaryExp(UnaryExpContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    UnaryExpr unaryExpr = new UnaryExpr(UNOP.get(ctx.unary_oper().getText()),
        expr);
    String errorMessage = unaryExpr.isTypeCompatible();
    if (errorMessage != null) {
      parser.notifyErrorListeners(ctx.expr().start,
          incompatibleMsg(ctx.expr().getText()) + errorMessage
          , null);
    }
    return unaryExpr;
  }

  @Override
  public Returnable visitIdentExp(IdentExpContext ctx) {
    String varName = ctx.IDENT().getText();
    Type varTypeDef = currentST.lookUpAllVar(varName);
    if (varTypeDef == null) {
      parser.notifyErrorListeners(ctx.start,
          identifierNotDefinedMsg(annotateVar(ctx.getText())), null);
      varTypeDef = new GenericType();
    }
    return new Ident(varName, varTypeDef);
  }

  @Override
  public Returnable visitArrayElemLhs(ArrayElemLhsContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public Returnable visitArrayExp(ArrayExpContext ctx) {
    return visit(ctx.array_elem());
  }

  @Override
  public Returnable visitArray_elem(Array_elemContext ctx) {
    String varName = ctx.IDENT().getText();
    int dimensionAccessed = ctx.expr().size();

    Type varTypeDef = currentST.lookUpAllVar(varName);
    if (varTypeDef == null) {
      parser
          .notifyErrorListeners(ctx.IDENT().getSymbol(),
              identifierNotDefinedMsg(annotateVar(varName)), null);
    }
    else if (!(varTypeDef instanceof ArrType)) {
      parser.notifyErrorListeners(ctx.IDENT().getSymbol(),
          incompatibleTypesMsg(varName, new ArrType(new GenericType()),
              varTypeDef), null);
    }
    else if (((ArrType) varTypeDef).dimension() < dimensionAccessed) {
      // TODO refactor
      String msg = incompatibleTypesMsg(varName,
          new ArrType(new GenericType()), varTypeDef);
      parser.notifyErrorListeners(ctx.IDENT().getSymbol(), msg.substring(0,
          msg.length() - 1) + ArrType.bracketsString(dimensionAccessed) + ')', null);
    }
    else {
      Expr[] indexes = new Expr[dimensionAccessed];
      for (int i = 0; i < dimensionAccessed; i++) {
        indexes[i] = (Expr) visit(ctx.expr(i));
      }
      return new ArrayElem(((ArrType) varTypeDef).getArrayElem(dimensionAccessed), varName,
          indexes);
//      return new ArrayElem(var.type(), varName,
//          (Expr[]) ctx.expr().stream().map(this::visit).toArray());
    }
    return null;
  }

  @Override
  public Returnable visitNewPair(NewPairContext ctx) {
    Expr fst = (Expr) visit(ctx.expr(0));
    Expr snd = (Expr) visit(ctx.expr(1));

    return new Pair(fst, snd);
  }

  @Override
  public Returnable visitAssignArray(AssignArrayContext ctx) {
    Array_literContext context = ctx.array_liter();
    Expr[] elems = new Expr[context.expr().size()];

    Type elemType = null;
    for (int i = 0; i < elems.length; i++) {
      ExprContext e = context.expr().get(i);
      Expr expr = (Expr) visit(e);

      if (elemType == null) {
        elemType = expr.type();
      }
      else if (!expr.type().equals(elemType)) {
        parser.notifyErrorListeners(e.start, incompatibleTypesMsg(e.getText()
            , elemType, expr.type()), null);
      }
      elems[i] = expr;
    }
    return new ArrayLiter(elems, ArrType.getArrayType(elemType));
  }

  @Override
  public Returnable visitBaseType(BaseTypeContext ctx) {
    return new BasicType(BasicType.TYPE.get(ctx.getText()));
  }

  @Override
  public Returnable visitArrayType(ArrayTypeContext ctx) {
    Type elemType = (Type) visit(ctx.type());
    return (elemType instanceof ArrType) ?
        ((ArrType) elemType).addDimension() : new ArrType(elemType);
  }

  @Override
  public Returnable visitStringType(StringTypeContext ctx) {
    return new ArrType(new BasicType(TYPE.CHAR));
  }

  @Override
  public Returnable visitPairType(PairTypeContext ctx) {
    Pair_typeContext context = ctx.pair_type();
    Type lhs = (Type) visit(context.pair_elem_type(0));
    Type rhs = (Type) visit(context.pair_elem_type(1));
    return new PairType(lhs, rhs);
  }

  @Override
  public Returnable visitPairElemBaseType(PairElemBaseTypeContext ctx) {
    String s = ctx.getText();
    TYPE t = BasicType.TYPE.get(s);
    return new BasicType(t);
  }

  @Override
  public Returnable visitPairElemArrayType(PairElemArrayTypeContext ctx) {
    return new ArrType((Type) visit(ctx.type()));
  }

  @Override
  public Returnable visitPairElemPairType(PairElemPairTypeContext ctx) {
    return new PairType(new GenericType(), new GenericType());
  }

  @Override
  public Returnable visitExitStat(ExitStatContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    if (!expr.type().equals(new BasicType(BasicType.TYPE.INT))) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " at character: "
              + ctx.expr().getStop().getCharPositionInLine()
              + ", exit statement requires: INT, found: " + expr.type()
              .toString());
    }
    currentParentNode.add(new ExitNode(expr));
    return null;
  }

  @Override
  public Returnable visitReturnStat(ReturnStatContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    Type exprType = expr.type();

    if (!currentST.isFunctionScope()) {
      parser.notifyErrorListeners(ctx.start, globalReturnMsg(), null);
      return null;
    }

    Type funcDefinitionReturn = currentST.lookUpAllVar("return");

    if (!funcDefinitionReturn.equals(exprType)) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + ", character:"
              + ctx.expr().getStart().getCharPositionInLine()
              + ", type mismatch: " + " (expected: "
              + funcDefinitionReturn.toString()
              + ", actual: " + exprType.toString() + ")");
    }
    currentParentNode.add(new ReturnNode(expr));
    return null;
  }

  @Override
  public Returnable visitAssignStat(AssignStatContext ctx) {
    NodeElem lhs = (NodeElem) visit(ctx.assign_lhs());
    NodeElem rhs = (NodeElem) visit(ctx.assign_rhs());
    if (!lhs.type().equals(rhs.type())) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + ", character:"
              + ctx.stop.getCharPositionInLine()
              + ", type mismatch: " + " (expected: "
              + lhs.type().toString()
              + ", actual: " + rhs.type().toString() + ")");
    }
    currentParentNode.add(new VarAssignNode(lhs, rhs));
    return null;
  }

  @Override
  public Returnable visitIdentLhs(IdentLhsContext ctx) {
    String varName = ctx.IDENT().getText();
    Type varTypeDef = currentST.lookUpAllVar(varName);
    if (varTypeDef == null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : variable "
              + varName + " is not defined in this scope");
      varTypeDef = new GenericType();
    }
    return new Ident(varName, varTypeDef);
  }

  @Override
  public Returnable visitFuncCall(FuncCallContext ctx) {
    String funcName = ctx.IDENT().getText();
    FuncTypes funcTypes = currentST.lookUpAllFunc(funcName);
    ListExpr args = new ListExpr();

    if (funcTypes == null) {
      parser.notifyErrorListeners(ctx.IDENT().getSymbol(),
          identifierNotDefinedMsg(annotateFunc(funcName)), null);
      return new FuncCall(funcName, args, new GenericType());
    } else {
      if (ctx.arg_list() != null) {
        args = (ListExpr) visit(ctx.arg_list());
      }
      List<Type> paramsTypes = funcTypes.getParamTypes();
      if (!ListExpr.hasSameTypes(args.getExprTypes(),paramsTypes)) {
        parser.notifyErrorListeners(
            "Semantic error at line: " + ctx.start.getLine() + " : function "
                + funcName + " has conflicting parameters and arguments, "
                + "expected: " + paramsTypes.toString() + ", " + "actual: " + args
                .toString());
      }
      return new FuncCall(funcName, args, funcTypes.getReturnType());
    }
  }

  @Override
  public Returnable visitArg_list(Arg_listContext ctx) {
    ListExpr argsList = new ListExpr();
    ctx.expr().forEach(e -> argsList.add(((Expr) visit(e))));
    return argsList;
  }

  @Override
  public Returnable visitFreeStat(BasicParser.FreeStatContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    if (!(expr.type() instanceof ArrType) && !(expr
        .type() instanceof PairType)) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " at character: "
              + ctx.expr().getStop().getCharPositionInLine()
              + ", free statement requires: pair(T1,T2) or T[], found: " + expr
              .type().toString());
    }
    currentParentNode.add(new FreeNode(expr));
    return null;
  }

  @Override
  public Returnable visitNewScopeStat(NewScopeStatContext ctx) {
    ScopeData stat = visitStatInNewScope(ctx.stat());
    currentParentNode.add(new ScopeNode(stat.astNode(), stat.symbolTable()));
    return null;
  }

  @Override
  public Returnable visitBracketExp(BracketExpContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    expr.putBrackets();
    return expr;
  }

  @Override
  public Returnable visitPair_elem(Pair_elemContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    if (!(expr.type() instanceof PairType)) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine()
              + " : type of argument is "
              + expr.type().toString() + ", should be pair");
      return null;
    }
    return getPairElem(expr, ctx);
  }

  @Override
  public Returnable visitPairExp(PairExpContext ctx) {
    return new PairExp(new PairType(new GenericType(), new GenericType()));
  }

  private Returnable getPairElem(Expr expr, Pair_elemContext ctx) {
    Type type;
    int pos;
    if (ctx.SND() == null) {
      type = ((PairType) expr.type()).getFst();
      pos = 1;
    } else {
      type = ((PairType) expr.type()).getSnd();
      pos = 2;
    }
    return new PairElem(type, pos);
  }

  private ScopeData visitStatInNewScope(StatContext stat) {
    ParentNode ParentNode = enterScope();
    visit(stat);
    return exitScope(ParentNode);
  }

  private ScopeData visitFuncStatInNewScope(String funcName,
      StatContext stat,
      Param_listContext paramListContext,
      Type funcReturnType) {

    ParentNode ParentNode = enterScope();
    currentST.setFunctionScope(true);
    currentST.addVar("return", funcReturnType);

    ListExpr paramList = new ListExpr();
    if (paramListContext != null) {
      paramList = (ListExpr) visit(paramListContext);
    }

    visit(stat);
    ScopeData sd = exitScope(ParentNode);
    return new ScopeData(sd.astNode(), sd.symbolTable(), paramList);
  }

  private ParentNode enterScope() {
    boolean inFuncScope = currentST.isFunctionScope();
    currentST = new SymbolTable(currentST);
    currentST.setFunctionScope(inFuncScope);
    ParentNode parentParentNode = currentParentNode;
    currentParentNode = new ParentNode();
    return parentParentNode;
  }

  private ScopeData exitScope(ParentNode parentParentNode) {
    ScopeData scopeData = new ScopeData(currentParentNode, currentST);
    currentST = currentST.getEncSymTable();
    currentParentNode = parentParentNode;
    return scopeData;
  }

  private void checkBoolExpr(ExprContext ctx, Expr condition) {
    if (!condition.type().equals(new BasicType(BasicType.TYPE.BOOL))) {
      parser.notifyErrorListeners(
          "Semantic error at line " + ctx.start.getLine()
              + ": Incompatible type at " + ctx.getText() + " (expected: "
              + "BOOL, actual:" + condition.type().toString() + ")");
    }
  }

  public class ScopeData {

    private ParentNode parentNode;
    private SymbolTable symbolTable;
    private ListExpr paramList;

    public ScopeData(ParentNode parentNode, SymbolTable symbolTable) {
      this.parentNode = parentNode;
      this.symbolTable = symbolTable;
      paramList = null;
    }

    public ScopeData(ParentNode parentNode, SymbolTable symbolTable,
        ListExpr paramList) {
      this.parentNode = parentNode;
      this.symbolTable = symbolTable;
      this.paramList = paramList;
    }

    public ParentNode astNode() {
      return parentNode;
    }

    public SymbolTable symbolTable() {
      return symbolTable;
    }

    public ListExpr paramList() {
      return paramList;
    }

  }

  private boolean isAssignSameType(Type varType, NodeElem rhs) {
    return (rhs instanceof ArrayLiter && ((ArrayLiter) rhs).isEmpty())
        || varType.equals(rhs.type());
  }

  private boolean isReadableType(NodeElem lhs) {
    return lhs.type() instanceof BasicType &&
        (((BasicType) lhs.type()).type().equals(TYPE.INT)
            || ((BasicType) lhs.type()).type().equals(TYPE.CHAR));
  }

  private String identifierAlreadyDefinedMsg(String offendingSymbol) {
    return offendingSymbol + " is already defined in this scope.";
  }

  private String identifierNotDefinedMsg(String offendingSymbol) {
    return offendingSymbol + " is not defined.";
  }

  private String globalReturnMsg() {
    return "Return statement must be in a function scope";
  }

  private String incompatibleReadType(String offendingSymbol, Type actual) {
    return "Incompatible type at " + offendingSymbol
        + " (expected: Int or Char, actual: "
        + actual.toString();
  }

  private String incompatibleTypesMsg(String offendingSymbol,
      Type expected,
      Type actual) {
    return incompatibleMsg(offendingSymbol)
        + " (expected: "
        + ((expected instanceof ArrType) ? "Any[]" : expected.toString())
        + ", actual: " + actual.toString()
        + ")";
  }

  private String incompatibleTypeListMsg(String offendingSymbol,
      List<Type> expected, List<Type> actual) {
    StringBuilder sb = new StringBuilder();
    sb.append(incompatibleMsg(offendingSymbol))
        .append(" (expected: ");
    for(Type t : expected) {
      sb.append((t instanceof ArrType) ? "Any[]" : t.toString());
    }
    sb.append(", actual: ");
    actual.forEach(t -> sb.append(t.toString()));
    sb.append(")");
    return sb.toString();
  }

  private String incompatibleMsg(String offendingSymbol) {
    return "Incompatible type at " + offendingSymbol;
  }

  private String annotateFunc(String identifier) {
    return "Function " + identifier;
  }

  private String annotateVar(String identifier) {
    return "Variable " + identifier;
  }


}
