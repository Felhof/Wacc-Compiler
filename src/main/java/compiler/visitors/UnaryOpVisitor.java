package compiler.visitors;

import antlr.*;
import antlr.BasicParser.UnaryExpContext;
import antlr.BasicParser.IntExpContext;
import antlr.BasicParser.BoolExpContext;
import antlr.BasicParser.CharExpContext;
import antlr.BasicParser.StrExpContext;
import antlr.BasicParser.PairExpContext;
import antlr.BasicParser.IdentExpContext;
import antlr.BasicParser.ArrayExpContext;
import antlr.BasicParser.BracketExpContext;
import antlr.BasicParser.BinaryExpContext;


public class UnaryOpVisitor extends BasicParserBaseVisitor<Boolean>{

  private BasicParser parser;
  private boolean negativeExpr = false;

  public UnaryOpVisitor(BasicParser parser) {
    this.parser = parser;
  }


  @Override
  public Boolean visitIntExp(IntExpContext ctx) {
    return true;
  }

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
  public Boolean visitBracketExp(BracketExpContext ctx){
    return true;
  }

  @Override
  public Boolean visitPairExp(PairExpContext ctx){
    return false;
  }

  @Override
  public Boolean visitBinaryExp(BinaryExpContext ctx){
    visit(ctx.expr(0));
    visit(ctx.expr(1));
    return true;
  }


  @Override
  public Boolean visitUnaryExp(UnaryExpContext ctx){

    if(ctx.unary_oper().PLUS() != null) {

      boolean valid = visit(ctx.expr());

      if (!valid) {
          parser.notifyErrorListeners("Syntactic Error at "+ ctx.start.getLine() + ":"
            + ctx.start.getCharPositionInLine() +  "-- mismatched input '"
            + ctx.expr().getText().charAt(0) + "' expecting INTEGER");
      }
    }

    return true;
  }
}
