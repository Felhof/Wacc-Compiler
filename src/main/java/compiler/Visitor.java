package compiler;

import antlr.*;
import antlr.BasicParser.ExitStatContext;
import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.ReturnStatContext;
import antlr.BasicParser.WhileStatContext;

public class Visitor extends BasicParserBaseVisitor<Boolean>{

  private BasicParser parser;

  public Visitor(BasicParser parser) {
    this.parser = parser;
  }

  @Override
  public Boolean visitFunc(BasicParser.FuncContext ctx) {
    if(visit(ctx.stat()) == null) {
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

}
