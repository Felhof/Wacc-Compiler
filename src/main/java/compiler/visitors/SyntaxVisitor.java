package compiler.visitors;import antlr.*;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;

public class SyntaxVisitor extends BasicParserBaseVisitor<Void>{

  private BasicParser parser;

  public SyntaxVisitor(BasicParser parser) {
    this.parser = parser;
  }



  @Override
  public Void visitExpr(BasicParser.ExprContext ctx) {


    String op = ctx.getParent().getChild(0).getText();

    String value = ctx.getText();

    if(op.equals("-")){
      value = "-" + value;
    }

    if(ctx.INTEGER() != null){

      CheckInteger(value);
    }

    visitChildren(ctx);

    return null;
  }


  @Override
  public Void visitUnary_oper(BasicParser.Unary_operContext ctx){

    String operatorText = ctx.getText();

    System.out.println("StartLine: " + ctx.start.getLine());
    System.out.println("StopLine: " + ctx.stop.getLine());


    BasicParser.ExprContext expr = (BasicParser.ExprContext) ctx.getParent().getChild(1);

    String childText = expr.getText();

    char c = childText.charAt(0);

    switch (operatorText){
      case "+":
        if(c == '"' || c == '\'' || c == '(' || c == ')'){
          parser.notifyErrorListeners("Missmatched Input '" + c + "'. Expecting Integer");
        }
        break;
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

