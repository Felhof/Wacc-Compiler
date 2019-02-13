package compiler.visitors;

import antlr.BasicParser;
import antlr.BasicParser.ArrayExpContext;
import antlr.BasicParser.BinaryExpContext;
import antlr.BasicParser.BoolExpContext;
import antlr.BasicParser.BracketExpContext;
import antlr.BasicParser.CharExpContext;
import antlr.BasicParser.ExitStatContext;
import antlr.BasicParser.IdentExpContext;
import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.IntExpContext;
import antlr.BasicParser.PairExpContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.ReturnStatContext;
import antlr.BasicParser.StrExpContext;
import antlr.BasicParser.UnaryExpContext;
import antlr.BasicParser.WhileStatContext;
import antlr.BasicParserBaseVisitor;

public class SyntaxVisitor extends BasicParserBaseVisitor<Boolean> {
  private BasicParser parser;
  private boolean negativeExpr = false;

  public SyntaxVisitor(BasicParser parser) {
    this.parser = parser;
  }

  /***************** Return check ********************/

  @Override
  public Boolean visitFunc(BasicParser.FuncContext ctx) {
    if(visit(ctx.stat()) == null  || !visit(ctx.stat())) {
      parser.notifyErrorListeners("No return statement");
    }
    return null;
  }

  @Override
  public Boolean visitIfStat(IfStatContext ctx) {
    return (visit(ctx.getChild(3)) != null) && (visit(ctx.getChild(5)) != null);
  }

  @Override
  public Boolean visitWhileStat(WhileStatContext ctx) {
    return visit(ctx.getChild(3));
  }

  @Override
  public Boolean visitRecursiveStat(RecursiveStatContext ctx) {
    return visit(ctx.getChild(2));
  }

  @Override
  public Boolean visitReturnStat(ReturnStatContext ctx) {
    return true;
  }

  @Override
  public Boolean visitExitStat(ExitStatContext ctx) {
    return true;
  }

  /***************** end of return check ********************/

  /***************** int range check ************************/

  @Override
  public Boolean visitIntExp(IntExpContext ctx) {
    checkIntRange(ctx.INTEGER().getText(), ctx);
    return true; // unary type check
  }

  @Override
  public Boolean visitBracketExp(BracketExpContext ctx) {
    negativeExpr = false;
    return true; // unary type check
  }

  @Override
  public Boolean visitBinaryExp(BinaryExpContext ctx) {
    visit(ctx.expr(0));
    visit(ctx.expr(1));
    negativeExpr = false;
    return true; // unary type check
  }

  @Override
  public Boolean visitUnaryExp(UnaryExpContext ctx) {
    if (ctx.unary_oper().MINUS() != null // unary operator type check
        || ctx.unary_oper().PLUS() != null
        || ctx.unary_oper().CHR() != null) {
      if (ctx.unary_oper().MINUS() != null) {
        negativeExpr = true;
      }
      if(!visit(ctx.expr())) {
        parser.notifyErrorListeners(ctx.expr().start, "Incorrect unary "
            + "operator argument type (expecting: INTEGER)" , null);
      }
    }
    return visit(ctx.expr());
  }

  public void checkIntRange(String value, IntExpContext ctx) {
    if (negativeExpr) {
      value = "-" + value;
    }
    try {
      Integer.parseInt(value);
    } catch (NumberFormatException e) {
      parser.notifyErrorListeners(ctx.start, "Integer overflow: " + value +
              " is out of integer range",
          null);
    }
  }

  /***************** end of int range check *****************/

  /***************** unary symbol type check ****************/

  @Override
  public Boolean visitBoolExp(BoolExpContext ctx){
    return false;
  }

  @Override
  public Boolean visitCharExp(CharExpContext ctx){
    return false;
  }

  @Override
  public Boolean visitStrExp(StrExpContext ctx){
    return false;
  }

  @Override
  public Boolean visitIdentExp(IdentExpContext ctx){
    return true;
  }
  @Override
  public Boolean visitArrayExp(ArrayExpContext ctx){
    return true;
  }

  @Override
  public Boolean visitPairExp(PairExpContext ctx){
    return false;
  }

  /***************** end of unary symbol type check *********/

}
