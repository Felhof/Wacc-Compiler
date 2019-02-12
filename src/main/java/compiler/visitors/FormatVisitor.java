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



public class FormatVisitor extends BasicParserBaseVisitor<Boolean>{

  private BasicParser parser;
  private boolean negativeExpr = false;

  public FormatVisitor(BasicParser parser) {
    this.parser = parser;
  }


  @Override
  public Boolean visitIntExp(IntExpContext ctx) {

    String value = ctx.INTEGER().getText();

     boolean validInteger = CheckInteger(value);

     if(!negativeExpr && !validInteger){
       parser.notifyErrorListeners("Integer value " + value + " on line " + ctx.start.getLine() + " is badly " +
               "formatted (either it has a badly defined sign or it is too large for a 32-bit signed integer)");
     }

     negativeExpr = false;
     return validInteger;

  }

  @Override
  public Boolean visitBoolExp(BoolExpContext ctx){
    negativeExpr = false;

    return true;
  }

  @Override
  public Boolean visitCharExp(CharExpContext ctx){
    negativeExpr = false;

    return true;
  }

  @Override
  public Boolean visitStrExp(StrExpContext ctx){
    negativeExpr = false;

    return true;
  }

  @Override
  public Boolean visitIdentExp(IdentExpContext ctx){
    negativeExpr = false;

    return true;
  }
  @Override
  public Boolean visitArrayExp(ArrayExpContext ctx){
    negativeExpr = false;

    return true;
  }

  @Override
  public Boolean visitBracketExp(BracketExpContext ctx){
    negativeExpr = false;

    return true;
  }


  @Override
  public Boolean visitPairExp(PairExpContext ctx){
    negativeExpr = false;

    return true;
  }

  @Override
  public Boolean visitBinaryExp(BinaryExpContext ctx){

    visit(ctx.expr(0));
    visit(ctx.expr(1));

    negativeExpr = false;

    return true;
  }


  @Override
  public Boolean visitUnaryExp(UnaryExpContext ctx){

    if(ctx.unary_oper().MINUS() != null) {
      negativeExpr = true;
    }

    boolean validPositive = visit(ctx.expr());


    if(!validPositive){
      String value = "-" + ctx.expr().getText();
      boolean validNegative = CheckInteger(value);

      if(!validNegative){
        parser.notifyErrorListeners("Integer value " + value + " on line " + ctx.start.getLine() + " is badly " +
                "formatted (either it has a badly defined sign or it is too large for a 32-bit signed integer)");
      }
    }

    return true;
  }



  public boolean CheckInteger(String value){

    try {
      Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return false;
    }

    return true;

  }
}

