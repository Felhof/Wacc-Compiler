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
  public  Void visitExpr(BasicParser.ExprContext ctx) {


    if(ctx.INTEGER() != null){

      String value = ctx.getText();

      try {
        Integer.parseInt(value);
      } catch (NumberFormatException e) {
        parser.notifyErrorListeners("Badly formated Integer");
      }

    }
    
    return null;
  }
}

