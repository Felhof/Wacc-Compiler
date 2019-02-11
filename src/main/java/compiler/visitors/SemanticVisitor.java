package compiler.visitors;

import antlr.BasicParser.Arg_listContext;
import antlr.BasicParser.ArrayTypeContext;
import antlr.BasicParser.Array_literContext;
import antlr.BasicParser.AssignArrayContext;
import antlr.BasicParser.AssignLhsContext;
import antlr.BasicParser.BaseTypeContext;
import antlr.BasicParser;
import antlr.BasicParser.BinaryExpContext;
import antlr.BasicParser.BoolExpContext;
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
import antlr.BasicParser.PairElemArrayTypeContext;
import antlr.BasicParser.PairElemBaseTypeContext;
import antlr.BasicParser.PairElemPairTypeContext;
import antlr.BasicParser.PairTypeContext;
import antlr.BasicParser.ParamContext;
import antlr.BasicParser.Param_listContext;
import antlr.BasicParser.Pair_typeContext;
import antlr.BasicParser.PrintStatContext;
import antlr.BasicParser.PrintlnStatContext;
import antlr.BasicParser.ProgContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.ReturnStatContext;
import antlr.BasicParser.StatContext;
import antlr.BasicParser.StrExpContext;
import antlr.BasicParser.UnaryExpContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParser.WhileStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.visitors.Identifiers.Function;
import compiler.visitors.Identifiers.Identifier;
import compiler.visitors.NodeElements.LHS.GenericLHS;
import compiler.visitors.NodeElements.RHS.ArrayLiter;
import compiler.visitors.NodeElements.LHS.AssignLHS;
import compiler.visitors.NodeElements.RHS.AssignRHS;
import compiler.visitors.NodeElements.RHS.FuncCall;
import compiler.visitors.NodeElements.LHS.IdentLHS;
import compiler.visitors.NodeElements.Types.ArrType;
import compiler.visitors.NodeElements.Types.BasicType;
import compiler.visitors.NodeElements.RHS.IdentExpr;
import compiler.visitors.NodeElements.RHS.Pair;
import compiler.visitors.NodeElements.TypeList;
import compiler.visitors.NodeElements.Types.GenericType;
import compiler.visitors.NodeElements.Types.PairType;
import compiler.visitors.NodeElements.Types.Type;
import compiler.visitors.NodeElements.RHS.UnaryExpr;
import compiler.visitors.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.visitors.Nodes.ASTNode;
import compiler.visitors.Nodes.ExitNode;
import compiler.visitors.Nodes.FuncNode;
import compiler.visitors.Nodes.IfElseNode;
import compiler.visitors.Nodes.PrintNode;
import compiler.visitors.Nodes.ReturnNode;
import compiler.visitors.Nodes.VarAssignNode;
import compiler.visitors.Nodes.VarDeclareNode;
import compiler.visitors.NodeElements.RHS.BinExpr;
import compiler.visitors.NodeElements.RHS.BinExpr.BINOP;
import compiler.visitors.NodeElements.RHS.BoolExpr;
import compiler.visitors.NodeElements.RHS.CharExpr;
import compiler.visitors.NodeElements.RHS.Expr;
import compiler.visitors.NodeElements.RHS.IntExpr;
import compiler.visitors.NodeElements.RHS.StringExpr;
import compiler.visitors.Identifiers.Variable;
import compiler.visitors.Nodes.WhileNode;

public class SemanticVisitor extends BasicParserBaseVisitor<Returnable> {

  private BasicParser parser;
  private SymbolTable currentST;
  private ASTNode currentASTNode;

  public SemanticVisitor(BasicParser parser) {
    this.parser = parser;
    currentST = new SymbolTable(null);
  }

  @Override
  public ASTNode visitProg(ProgContext ctx) {
    currentASTNode = new ASTNode();
    ctx.func().forEach(this::visitFunc);
    visit(ctx.stat(0));
    return currentASTNode;
  }

  @Override
  public Returnable visitFunc(FuncContext ctx) {
    Type funcReturnType = (Type) visit(ctx.type());
    if (currentST.lookUpAll(ctx.IDENT().getText()) != null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : function "
              + ctx.IDENT() + " has already been defined in this scope");
    }

    ScopeData funcStat = visitFuncStatInNewScope(ctx.stat(), ctx.param_list(), funcReturnType);

    currentASTNode.add(new FuncNode(funcReturnType,
        ctx.IDENT().getText(),
        funcStat.paramList(), funcStat.astNode(),
        funcStat.symbolTable()));
    currentST.add(ctx.IDENT().getText(),
         new Function(funcStat.paramList(), funcReturnType));

    return null;
  }

  @Override
  public Returnable visitParam_list(Param_listContext ctx) {
    TypeList paramList = new TypeList();
    String txt = ctx.param(0).getText();
    System.out.println(txt);
    ctx.param().forEach(p -> paramList.add((Type) visit(p)));
    return paramList;
  }

  @Override
  public Returnable visitParam(ParamContext ctx) {
    Type paramType = (Type) visit(ctx.type());
    currentST.add(ctx.IDENT().getText(), new Variable(paramType));
    return paramType;
  }

  @Override
  public ASTNode visitRecursiveStat(RecursiveStatContext ctx) {
    visit(ctx.stat(0));
    visit(ctx.stat(1));
    return null;
  }

  @Override
  public Returnable visitPrintStat(PrintStatContext ctx) {
    currentASTNode.add(new PrintNode(false, (Expr) visit(ctx.expr())));
    return null;
  }

  @Override
  public Returnable visitPrintlnStat(PrintlnStatContext ctx) {
    currentASTNode.add(new PrintNode(true, (Expr) visit(ctx.expr())));
    return null;
  }

  @Override
  public Returnable visitIfStat(IfStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx.expr(), condition);

    ScopeData thenStat = visitStatInNewScope(ctx.stat(0));
    ScopeData elseStat = visitStatInNewScope(ctx.stat(1));

    currentASTNode.add(new IfElseNode(condition, thenStat.astNode(),
        thenStat.symbolTable(), elseStat.astNode(), elseStat.symbolTable()));
    return null;
  }

  @Override
  public Returnable visitWhileStat(WhileStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx.expr(), condition);

    ScopeData stat = visitStatInNewScope(ctx.stat());

    currentASTNode.add(new WhileNode(condition, stat.astNode(), stat.symbolTable()));

    return null;
  }

  @Override
  public ASTNode visitVarDeclarationStat(VarDeclarationStatContext ctx) {
    String varName = ctx.IDENT().getText();

    Type varType = (Type) visit(ctx.type());
    Identifier var = currentST.lookUpScope(varName);
    AssignRHS rhs = (AssignRHS) visit(ctx.assign_rhs()); // simple case

    if(!isAssignSameType(varType, rhs)) {
      parser
          .notifyErrorListeners("Semantic error at line " + ctx.start.getLine()
              + ". Type mismatch");
    }

    if (var != null) {
      parser
          .notifyErrorListeners("Semantic error at line " + ctx.start.getLine()
              + ". Variable name is already declared in scope");
    } else {
      currentST.add(varName, new Variable(varType));
      currentASTNode.add(new VarDeclareNode(varName, rhs));
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
    BinExpr binExpr = new BinExpr(lhs, BINOP.get(ctx.binary_oper().getText()), rhs);
    String errorMessage = binExpr.isTypeCompatible();
    if (errorMessage != null) {
      parser.notifyErrorListeners("Semantic error at line: "
          + ctx.start.getLine() + ": " + errorMessage);
    }
    return binExpr;
  }

  @Override
  public Returnable visitUnaryExp(UnaryExpContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    UnaryExpr unaryExpr = new UnaryExpr(UNOP.get(ctx.unary_oper().getText()),
        expr);
    String errorMessage = unaryExpr.isTypeCompatible();
    if(errorMessage != null) {
      parser.notifyErrorListeners("Semantic error at line: "
          + ctx.start.getLine() + ":" + ctx.expr().start.getCharPositionInLine()
          + ": Incompatible type at " + ctx.expr().getText() + errorMessage);
    }
    return unaryExpr;
  }

  @Override
  public Returnable visitIdentExp(IdentExpContext ctx) {
    String varName = ctx.IDENT().getText();
    Variable variable = (Variable) currentST.lookUpAll(varName);
    if (variable == null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : variable "
              + varName + " is not defined in this scope");
      variable = new Variable(new BasicType(BasicType.TYPE.RECOVERY));
    }
    return new IdentExpr(variable.type());
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
        parser.notifyErrorListeners("Incompatible type at " + e.getText()
            + " (expected: " + elemType.toString()
            + "actual: " + expr.type().toString()+ ")");
      }
      elems[i] = expr;
    }
    return new ArrayLiter(elems, elemType);
  }

  @Override
  public Returnable visitBaseType(BaseTypeContext ctx) {
    return new BasicType(BasicType.TYPE.get(ctx.getText()));
  }

  @Override
  public Returnable visitArrayType(ArrayTypeContext ctx) {
    return new ArrType((Type) visit(ctx.type()));
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
    return new BasicType(BasicType.TYPE.get(ctx.getText()));
  }

  @Override
  public Returnable visitPairElemArrayType(PairElemArrayTypeContext ctx) {
    return new ArrType((Type) visit(ctx.type()));
  }

  @Override
  public Returnable visitPairElemPairType(PairElemPairTypeContext ctx) {
    return new PairType(new BasicType(BasicType.TYPE.RECOVERY), new BasicType(BasicType.TYPE.RECOVERY));
  }

  @Override
  public Returnable visitExitStat(ExitStatContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    if (!expr.type().equals(new BasicType(BasicType.TYPE.INT))) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " at character: " + ctx.expr().getStop().getCharPositionInLine() + ", exit statement requires: INT, found: " + expr.type().toString());
    }
    currentASTNode.add(new ExitNode(expr));
    return null;
  }

  @Override
  public Returnable visitReturnStat(ReturnStatContext ctx) {
    Expr expr = (Expr) visit(ctx.expr());
    if (!currentST.isInFunctionScope()) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + ", character:"+ ctx.expr().getStart().getCharPositionInLine() + ", return statement is not in a function");
      return null;
    }

    Type funcDefinitionReturn = ((Variable) currentST.lookUpAll("return")).type();
    Type exprType = expr.type();

    if (!funcDefinitionReturn.equals(exprType)) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + ", character:"
              + ctx.expr().getStart().getCharPositionInLine()
              + ", type mismatch: " + " (expected: "
              + funcDefinitionReturn.toString()
              + ", actual: " + exprType.toString()+ ")");
    }
    currentASTNode.add(new ReturnNode(expr));
    return null;
  }

  //TODO
  @Override
  public Returnable visitAssignLhs(AssignLhsContext ctx) {
    AssignLHS lhs = (AssignLHS) visit(ctx.assign_lhs());
    AssignRHS rhs = (AssignRHS) visit(ctx.assign_rhs());
    if (!lhs.type().equals(rhs.type())) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + ", character:"
              + ctx.stop.getCharPositionInLine()
              + ", type mismatch: " + " (expected: "
              + lhs.type().toString()
              + ", actual: " + rhs.type().toString()+ ")");
    }

    currentASTNode.add(new VarAssignNode(lhs, rhs));
    return null ;
  }

  @Override
  public Returnable visitIdentLhs(IdentLhsContext ctx) {
    // TODO fix for function assign
    String varName = ctx.IDENT().getText();
    Identifier identifier =  currentST.lookUpAll(varName);
    if (identifier == null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : variable "
              + varName + " is not defined in this scope");
      identifier = new Variable(new BasicType(BasicType.TYPE.RECOVERY));
    } else if (identifier instanceof Function) {
      parser.notifyErrorListeners("Semantic error at line: " + ctx.start.getLine() + "Cannot assign right hand side statement to a function");
      return new GenericLHS(new GenericType());
    }
    return new IdentLHS(((Variable) identifier).type());
  }

  @Override
  public Returnable visitFuncCall(FuncCallContext ctx) {
    //return super.visitFuncCall(ctx);
    String funcName = ctx.IDENT().getText();
    Function function = (Function) currentST.lookUpAll(funcName);

    if (function == null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : function "
              + funcName + " is not defined in this scope");
      // variable = new Variable(new BasicType(TYPE.RECOVERY));
      return null;
    } else {

      TypeList args = new TypeList();
      if (ctx.arg_list() != null) {
        args = (TypeList) visit(ctx.arg_list());
      }

      TypeList params = function.getParamList();
      if (!args.equals(params)) {
        parser.notifyErrorListeners(
            "Semantic error at line: " + ctx.start.getLine() + " : function "
                + funcName + " has conflicting parameters and arguments, " + "expected: " + params.toString() + ", " + "actual: " + args.toString());
      }
      return new FuncCall(funcName, args, function.getType());
    }
  }

  @Override
  public Returnable visitArg_list(Arg_listContext ctx) {
    TypeList argsList = new TypeList();
    ctx.expr().forEach(e -> argsList.add(((Expr) visit(e)).type()));
    return argsList;
  }

  public ScopeData visitStatInNewScope(StatContext stat) {
    ASTNode ASTNode = enterScope();
    visit(stat);
    return exitScope(ASTNode);
  }

  public ScopeData visitFuncStatInNewScope(StatContext stat,
      Param_listContext paramListContext,
      Type funcReturnType) {
    ASTNode ASTNode = enterScope();
    currentST.setFunctionScope(true);
    currentST.add("return", new Variable(funcReturnType));
    TypeList paramList = new TypeList();
    if (paramListContext!= null) {
    paramList = (TypeList) visit(paramListContext);
    }
    visit(stat);
    ScopeData sd = exitScope(ASTNode);
    return new ScopeData(sd.astNode(), sd.symbolTable(), paramList);
  }

  private ASTNode enterScope() {
    currentST = new SymbolTable(currentST);
    ASTNode parentASTNode = currentASTNode;
    currentASTNode = new ASTNode();
    return parentASTNode;
  }

  private ScopeData exitScope(ASTNode parentASTNode) {
    ScopeData scopeData = new ScopeData(currentASTNode, currentST);
    currentST = currentST.getEncSymTable();
    currentASTNode = parentASTNode;
    return scopeData;
  }

  private void checkBoolExpr(ExprContext ctx, Expr condition) {
    if(!condition.type().equals(new BasicType(BasicType.TYPE.BOOL))) {
      parser.notifyErrorListeners(
          "Semantic error at line " + ctx.start.getLine()
              + ": Incompatible type at " + ctx.getText() + " (expected: "
              + "BOOL, actual:" +  condition.type().toString() + ")");
    }
  }

  public class ScopeData {
    private ASTNode astNode;
    private SymbolTable symbolTable;
    private TypeList paramList;

    public ScopeData(ASTNode astNode, SymbolTable symbolTable) {
      this.astNode = astNode;
      this.symbolTable = symbolTable;
      paramList = null;
    }

    public ScopeData(ASTNode astNode, SymbolTable symbolTable,
        TypeList paramList) {
      this.astNode = astNode;
      this.symbolTable = symbolTable;
      this.paramList = paramList;
    }

    public ASTNode astNode() {
      return astNode;
    }

    public SymbolTable symbolTable() {
      return symbolTable;
    }

    public TypeList paramList() {
      return paramList;
    }

  }

  private boolean isAssignSameType(Type varType, AssignRHS rhs) {
    return (rhs instanceof ArrayLiter && ((ArrayLiter) rhs).isEmpty())
        || varType.equals(rhs.type());
  }

}
