package compiler.visitors;

import antlr.BasicParser.BinaryExpContext;
import antlr.BasicParser.BoolExpContext;
import antlr.BasicParser.IdentExpContext;
import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.ProgContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.visitors.Nodes.IfElseNode;
import compiler.visitors.Nodes.ParentNode;
import compiler.visitors.Nodes.VarDeclareNode;
import compiler.visitors.identifiers.BinExpr;
import compiler.visitors.identifiers.BinExpr.BINOP;
import compiler.visitors.identifiers.BoolExpr;
import compiler.visitors.identifiers.Expr;
import compiler.visitors.identifiers.Identifier;
import compiler.visitors.identifiers.TYPE;
import compiler.visitors.identifiers.Variable;

public class SemanticVisitor extends BasicParserBaseVisitor<Returnable> {

  private SymbolTable currentST;
  private ParentNode currentASTNode;

  public SemanticVisitor() {
    currentST = new SymbolTable(null);
  }

  @Override
  public ParentNode visitProg(ProgContext ctx) {
    currentASTNode = new ParentNode();
//    ctx.func().forEach(this::visitFunc);
    visit(ctx.stat(0));
    return currentASTNode;
  }

  @Override
  public ParentNode visitRecursiveStat(RecursiveStatContext ctx) {
    visit(ctx.stat(0));
    visit(ctx.stat(1));
    return null;
  }

  @Override
  public Returnable visitIfStat(IfStatContext ctx) {
    Expr condition = (Expr) visit(ctx.expr());
    if(condition.type() != TYPE.BOOL) {
      System.out.println("Semantic error at line " + ctx.start.getLine() + ": if condition must evaluate to a boolean");
    }

    ParentNode parentNode = enterScope();
    visit(ctx.stat(0));
    ParentNode thenStat = exitScope(parentNode);

    enterScope();
    visit(ctx.stat(1));
    ParentNode elseStat = exitScope(parentNode);

    currentASTNode.add(new IfElseNode(condition, thenStat, elseStat));
    return null;
  }

  @Override
  public ParentNode visitVarDeclarationStat(VarDeclarationStatContext ctx) {
    String varName = ctx.IDENT().getText();

    TYPE varType = TYPE.get(ctx.type().getText());

    Variable var = (Variable) currentST.lookUpScope(varName);

    Expr rhs = (Expr) visit(ctx.assign_rhs()); // simple case

    if(rhs.type() != varType) {
      System.out.println("Semantic error at line " + ctx.start.getLine()
          + ". Type mismatch");
    }
    if (var != null) {
      System.out.println("Semantic error at line " + ctx.start.getLine()
          + ". Variable name is already declared in scope");
    }
    else {
      currentST.add(varName, new Variable(varType));
      currentASTNode.add(new VarDeclareNode(varName, rhs));
    }
    return null;
  }

  @Override
  public Returnable visitBoolExp(BoolExpContext ctx) {
    return new BoolExpr(ctx.bool_liter().getText().equals("true"));
  }


  private ParentNode enterScope() {
    currentST = new SymbolTable(currentST);
    ParentNode parentASTNode = currentASTNode;
    currentASTNode = new ParentNode();
    return parentASTNode;
  }

  private ParentNode exitScope(ParentNode parentASTNode) {
    currentST = currentST.getEncSymTable();
    ParentNode currentNode = currentASTNode;
    currentASTNode = parentASTNode;
    return currentNode;

  }


  @Override
  public Returnable visitBinaryExp(BinaryExpContext ctx) {
    Expr lhs = (Expr) visit(ctx.expr(0));
    Expr rhs = (Expr) visit(ctx.expr(1));
    BinExpr binExpr = new BinExpr(lhs, BINOP.get(ctx.binary_oper().getText()), rhs);
    if (!binExpr.isTypeCompatible()) {
      System.out.println("Semantic error at line: " + ctx.start.getLine()
          + ": type mismatch in binary expression");
    }
    return binExpr;
  }


  @Override
  public Returnable visitIdentExp(IdentExpContext ctx) {
    String varName = ctx.IDENT().getText();
    Variable variable = (Variable) currentST.lookUpAll(varName);
    if (variable == null) {
      System.out.println("Semantic error at line: " + ctx.start.getLine());
    }
    return new Expr(variable.type());
  }

}
