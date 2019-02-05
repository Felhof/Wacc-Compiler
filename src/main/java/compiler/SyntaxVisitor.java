package compiler;import antlr.*;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxVisitor extends BasicParserBaseVisitor<Void>{

  private BasicParser parser;

  public SyntaxVisitor(BasicParser parser) {
    this.parser = parser;
  }


  @Override
  public Void visitBinary_oper(BasicParser.Binary_operContext ctx){
    return null;
  }

  @Override
  public Void visitExpr(BasicParser.ExprContext ctx) {


    if(ctx.INTEGER() != null){

      CheckInteger(ctx.getText());
    }

    visitChildren(ctx);

    return null;
  }

/*

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
  public Void visitStat(BasicParser.StatContext ctx){
    return null;
  }


  @Override
  public Void visitStr_liter(BasicParser.Str_literContext ctx){
    return null;
  }*/


  @Override
  public Void visitUnary_oper(BasicParser.Unary_operContext ctx){

    BasicParser.ExprContext expr = (BasicParser.ExprContext) ctx.getParent().getChild(1);

    String childText = expr.getText();

    char c = childText.charAt(0);

   if(c == '"' || c == '\'' || c == '(' || c == ')'){
          parser.notifyErrorListeners("Missmatched Input '" + c + "'. Expecting Integer");
    }

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

