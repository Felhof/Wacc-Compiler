package compiler;

import antlr.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor extends BasicParserBaseVisitor<Void>{

  private BasicParser parser;

  public Visitor(BasicParser parser) {
    this.parser = parser;
  }

  @Override
  public Void visitFunc(BasicParser.FuncContext ctx) {
    int lastChildIndex = ctx.getChildCount() - 2;
    ParseTree lastChild = ctx.getChild(lastChildIndex);
    TerminalNode returnToken = (TerminalNode) lastChild.getChild(0);
    if(returnToken.getSymbol().getType() != BasicLexer.RETURN) {
      parser.notifyErrorListeners("No return statement");
    }
    return null;
  }

}
