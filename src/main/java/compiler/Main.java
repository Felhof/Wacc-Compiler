package compiler;

import compiler.AST.Nodes.AST;
import compiler.listeners.SemanticErrorListener;
import compiler.listeners.SyntaxErrorListener;
import compiler.visitors.ReturnFunctionVisitor;
import compiler.visitors.SemanticVisitor;
import java.io.IOException;
import antlr.*;
import compiler.visitors.FormatVisitor;
import compiler.visitors.UnaryOpVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    //ParentNode ast = compileProg(args[0]); // uncomment for labTS test
    AST ast = compileProg("src/test/invalid/semanticErr/expressions/exprTypeErr.wacc");
    //System.out.println(ast.toString());
    System.exit(0);
  }

  public static AST compileProg(String filename) {
    BasicLexer lexer = lexFile(filename);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    return parser(tokenStream);
  }

  public static BasicLexer lexFile(String filename) {
    CharStream input = null;
    try {
      input = CharStreams.fromFileName(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new BasicLexer(input);
  }

  public static AST parser(CommonTokenStream stream) {
    BasicParser parser = new BasicParser(stream);

    parser.removeErrorListeners();
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.prog();
    // System.out.println(tree.toStringTree(parser));

    ReturnFunctionVisitor returnFunctionVisitor = new ReturnFunctionVisitor(parser);
    returnFunctionVisitor.visit(tree);
    FormatVisitor formatVisitor = new FormatVisitor(parser);
    formatVisitor.visit(tree);

    UnaryOpVisitor unaryOpVisitor = new UnaryOpVisitor(parser);
    unaryOpVisitor.visit(tree);

    syntaxErrorsExit(syntaxErrorListener.getNbSyntaxErrors());

    return semanticCheck(parser,tree);
    //return null;
  }

  public static AST semanticCheck(BasicParser parser, ParseTree tree) {
    parser.removeErrorListeners();
    SemanticErrorListener semanticErrorListener = new SemanticErrorListener();
    parser.addErrorListener(semanticErrorListener);
    SemanticVisitor semanticVisitor = new SemanticVisitor(parser);
    AST ast = (AST) semanticVisitor.visit(tree);
    semanticErrorListener.printCompilationStatus();
    return ast;
  }

  public static void syntaxErrorsExit(int nbSyntaxErrors) {
    if (nbSyntaxErrors > 0) {
      System.err.println(nbSyntaxErrors +" syntax error(s)");
      System.err.println("Exit code 100 returned");
      System.exit(100);
    }
  }



}

