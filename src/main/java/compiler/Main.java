package compiler;

import compiler.listeners.SemanticErrorListener;
import compiler.listeners.SyntaxErrorListener;
import compiler.visitors.Nodes.ASTNode;
import compiler.visitors.ReturnFunctionVisitor;
import compiler.visitors.SemanticVisitor;
import java.io.IOException;
import antlr.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    //ASTNode ast = compileProg(args[0]); // uncomment for labTS test
    ASTNode ast = compileProg("src/test/invalid/semanticErr/array/differentElemtypeArray.wacc");
    System.out.println(ast.toString());
    System.exit(0);
  }

  public static ASTNode compileProg(String filename) {
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

  public static ASTNode parser(CommonTokenStream stream) {
    BasicParser parser = new BasicParser(stream);

    parser.removeErrorListeners();
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.prog();
    // System.out.println(tree.toStringTree(parser));

    ReturnFunctionVisitor returnFunctionVisitor = new ReturnFunctionVisitor(parser);
    returnFunctionVisitor.visit(tree);
    //SyntaxVisitor syntaxVisitor = new SyntaxVisitor(parser);
    //syntaxVisitor.visit(tree);
    syntaxErrorsExit(syntaxErrorListener.getNbSyntaxErrors());

    return semanticCheck(parser,tree);
    //return null;
  }

  public static ASTNode semanticCheck(BasicParser parser, ParseTree tree) {
    parser.removeErrorListeners();
    SemanticErrorListener semanticErrorListener = new SemanticErrorListener();
    parser.addErrorListener(semanticErrorListener);
    SemanticVisitor semanticVisitor = new SemanticVisitor(parser);
    ASTNode ast = (ASTNode) semanticVisitor.visit(tree);
    semanticErrorsExit(semanticErrorListener.getNbSemanticErrors());
    return ast;
  }

  public static void syntaxErrorsExit(int nbSyntaxErrors) {
    if (nbSyntaxErrors > 0) {
      System.err.println(nbSyntaxErrors +" syntax error(s)");
      System.out.println("Exit code 100 returned");
      System.exit(100);
    }
  }

  public static void semanticErrorsExit(int nbSematicErrors) {
    if (nbSematicErrors > 0) {
      System.err.println(nbSematicErrors +" semantic error(s)");
      System.out.println("Exit code 200 returned");
      System.exit(200);
    }
  }

}

