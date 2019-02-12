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



public class SyntaxVisitor extends BasicParserBaseVisitor<Boolean>{

  private BasicParser parser;
  private boolean unaryExpr = false;

  public SyntaxVisitor(BasicParser parser) {
    this.parser = parser;
  }


  @Override
  public Boolean visitIntExp(IntExpContext ctx) {

    String value = ctx.INTEGER().getText();

     boolean validInteger = CheckInteger(value);

     if(!unaryExpr && !validInteger){
       parser.notifyErrorListeners("Integer value " + value + " on line " + ctx.start.getLine() + " is badly " +
               "formatted (either it has a badly defined sign or it is too large for a 32-bit signed integer)");
     }

     unaryExpr = false;
     return validInteger;

  }

  @Override
  public Boolean visitBoolExp(BoolExpContext ctx){
    unaryExpr = false;

    return true;
  }

  @Override
  public Boolean visitCharExp(CharExpContext ctx){
    unaryExpr = false;

    return true;
  }

  @Override
  public Boolean visitStrExp(StrExpContext ctx){
    unaryExpr = false;

    return true;
  }

  @Override
  public Boolean visitIdentExp(IdentExpContext ctx){
    unaryExpr = false;

    return true;
  }
  @Override
  public Boolean visitArrayExp(ArrayExpContext ctx){
    unaryExpr = false;

    return true;
  }

  @Override
  public Boolean visitBracketExp(BracketExpContext ctx){
    unaryExpr = false;

    return true;
  }


  @Override
  public Boolean visitPairExp(PairExpContext ctx){
    unaryExpr = false;

    return true;
  }

  @Override
  public Boolean visitBinaryExp(BinaryExpContext ctx){
    unaryExpr = false;

    return true;
  }





  @Override
  public Boolean visitUnaryExp(UnaryExpContext ctx){
    unaryExpr = true;

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

