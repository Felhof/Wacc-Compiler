package compiler;import antlr.*;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;

public class Visitor extends BasicParserBaseVisitor<Void>{

  private BasicParser parser;

  public Visitor(BasicParser parser) {
    this.parser = parser;
  }

  @Override
  public Void visitFunc(BasicParser.FuncContext ctx) {
    int lastToken = ctx.getChildCount() - 2;
    ParseTree node = ctx.getChild(lastToken);    System.out.println(ctx.type());
    //== BasicParser.RETURN
    CommonToken t;
    //parser.notifyErrorListeners("No return/exit statement");
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

