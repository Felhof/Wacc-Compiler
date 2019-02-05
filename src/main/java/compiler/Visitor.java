package compiler;import antlr.*;

public class Visitor extends BasicParserBaseVisitor<Void>{

  private BasicParser parser;

  public Visitor(BasicParser parser) {
    this.parser = parser;
  }

  @Override
  public Void visitFunc(BasicParser.FuncContext ctx) {
    return null;
  }

  @Override
  public Void visitExpr(BasicParser.ExprContext ctx) {


    if(ctx.INTEGER() != null){

      //CheckInteger(ctx.getText());
    }

    /* if(ctx.bool_liter() != null){
    }

    if(ctx.char_liter() != null){
    }

    if(ctx.str_liter() != null){
    }

    if(ctx.pair_liter() != null){
    }

    if(ctx.IDENT() != null) {
    }

    if(ctx.array_elem() != null) {
    }

    if(ctx.unary_oper() != null){
    }

    if (ctx.binary_oper() != null){
    }

    if(ctx.OPEN() != null){
    }

    if(ctx.CLOSE() != null){
    }*/

    return null;
  }

  @Override
  public Void visitBinary_oper(BasicParser.Binary_operContext ctx){

    return null;
  }

  public void CheckInteger(String value){

    try {
      Integer.parseInt(value);
    } catch (NumberFormatException e) {
      parser.notifyErrorListeners("Badly formated Integer");
    }

  }
}

