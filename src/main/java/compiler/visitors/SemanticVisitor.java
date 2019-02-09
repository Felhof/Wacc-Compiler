package compiler.visitors;

import antlr.BasicParser.BaseTypeContext;
import antlr.BasicParser;
import antlr.BasicParser.BinaryExpContext;
import antlr.BasicParser.BoolExpContext;
import antlr.BasicParser.CharExpContext;
import antlr.BasicParser.DefPairTypeContext;
import antlr.BasicParser.IdentExpContext;
import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.IntExpContext;
import antlr.BasicParser.NewPairContext;
import antlr.BasicParser.PairElemBaseTypeContext;
import antlr.BasicParser.PairElemPairTypeContext;
import antlr.BasicParser.PairTypeContext;
import antlr.BasicParser.ProgContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.StatContext;
import antlr.BasicParser.StrExpContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParser.WhileStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.visitors.NodeElements.AssignRHS;
import compiler.visitors.NodeElements.BasicType;
import compiler.visitors.NodeElements.IdentExpr;
import compiler.visitors.NodeElements.Pair;
import compiler.visitors.NodeElements.PairType;
import compiler.visitors.NodeElements.Type;
import compiler.visitors.Nodes.ASTNode;
import compiler.visitors.Nodes.IfElseNode;
import compiler.visitors.Nodes.VarDeclareNode;
import compiler.visitors.NodeElements.BinExpr;
import compiler.visitors.NodeElements.BinExpr.BINOP;
import compiler.visitors.NodeElements.BoolExpr;
import compiler.visitors.NodeElements.CharExpr;
import compiler.visitors.NodeElements.Expr;
import compiler.visitors.NodeElements.IntExpr;
import compiler.visitors.NodeElements.StringExpr;
import compiler.visitors.NodeElements.TYPE;
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
//    ctx.func().forEach(this::visitFunc);
    visit(ctx.stat(0));
    return currentASTNode;
  }

  @Override
  public ASTNode visitRecursiveStat(RecursiveStatContext ctx) {
    visit(ctx.stat(0));
    visit(ctx.stat(1));
    return null;
  }

  @Override
  public Returnable visitIfStat(IfStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx, condition);

    ScopeData thenStat = visitStatInNewScope(ctx.stat(0));
    ScopeData elseStat = visitStatInNewScope(ctx.stat(1));

    currentASTNode.add(new IfElseNode(condition, thenStat.astNode(),
        thenStat.symbolTable(), elseStat.astNode(), elseStat.symbolTable()));
    return null;
  }

  @Override
  public Returnable visitWhileStat(WhileStatContext ctx) {

    Expr condition = (Expr) visit(ctx.expr());
    checkBoolExpr(ctx, condition);

    ScopeData stat = visitStatInNewScope(ctx.stat());

    currentASTNode.add(new WhileNode(condition, stat.astNode(), stat.symbolTable()));

    return null;
  }

  @Override
  public ASTNode visitVarDeclarationStat(VarDeclarationStatContext ctx) {
    String varName = ctx.IDENT().getText();

    Type varType = (Type) visit(ctx.type());
    Variable var = (Variable) currentST.lookUpScope(varName);
    AssignRHS rhs = (AssignRHS) visit(ctx.assign_rhs()); // simple case

    if(!rhs.type().equals(varType)) {
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
    if (!binExpr.isTypeCompatible()) {
      parser
          .notifyErrorListeners("Semantic error at line: " + ctx.start.getLine()
              + ": type mismatch in binary expression");
    }
    return binExpr;
  }

  @Override
  public Returnable visitIdentExp(IdentExpContext ctx) {
    String varName = ctx.IDENT().getText();
    Variable variable = (Variable) currentST.lookUpAll(varName);
    if (variable == null) {
      parser.notifyErrorListeners(
          "Semantic error at line: " + ctx.start.getLine() + " : variable "
              + varName + " is not defined in this scope");
      variable = new Variable(new BasicType(TYPE.RECOVERY));
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
  public Returnable visitBaseType(BaseTypeContext ctx) {
    return new BasicType(TYPE.get(ctx.getText()));
  }

  @Override
  public Returnable visitPairType(PairTypeContext ctx) {
    return visit(ctx.pair_type());
  }

  @Override
  public Returnable visitDefPairType(DefPairTypeContext ctx) {
    Type lhs = (Type) visit(ctx.pair_elem_type(0));
    Type rhs = (Type) visit(ctx.pair_elem_type(1));
    return new PairType(lhs, rhs);
  }

  @Override
  public Returnable visitPairElemBaseType(PairElemBaseTypeContext ctx) {
    return new BasicType(TYPE.get(ctx.getText()));
  }

  @Override
  public Returnable visitPairElemPairType(PairElemPairTypeContext ctx) {
    return new PairType(null, null);
  }

  public ScopeData visitStatInNewScope(StatContext stat) {
    ASTNode ASTNode = enterScope();
    visit(stat);
    return exitScope(ASTNode);
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

  private void checkBoolExpr(StatContext ctx, Expr condition) {
    if(!condition.type().equals(new BasicType(TYPE.BOOL))) {
      parser.notifyErrorListeners(
          "Semantic error at line " + ctx.start.getLine()
              + ": if condition must evaluate to a boolean");    }
  }

  public class ScopeData {
    private ASTNode astNode;
    private SymbolTable symbolTable;

    public ScopeData(ASTNode astNode, SymbolTable symbolTable) {
      this.astNode = astNode;
      this.symbolTable = symbolTable;
    }

    public ASTNode astNode() {
      return astNode;
    }

    public SymbolTable symbolTable() {
      return symbolTable;
    }
  }



}
