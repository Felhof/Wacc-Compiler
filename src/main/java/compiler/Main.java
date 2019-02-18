package compiler;

import compiler.AST.Nodes.AST;
import compiler.listeners.ErrorListener;
import compiler.visitors.SemanticVisitor;
import compiler.visitors.SyntaxVisitor;
import java.io.IOException;
import antlr.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    AST ast = compileProg(args[0]); // uncomment for labTS test
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
    ErrorListener syntaxErrorListener = new ErrorListener("Syntax");
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.prog();

    SyntaxVisitor syntaxVisitor = new SyntaxVisitor(parser);
    syntaxVisitor.visit(tree);
    syntaxErrorListener.printCompilationStatus();

    return semanticCheck(parser,tree);
  }

  public static AST semanticCheck(BasicParser parser, ParseTree tree) {
    parser.removeErrorListeners();
    ErrorListener semanticErrorListener = new ErrorListener("Semantic");
    parser.addErrorListener(semanticErrorListener);
    SemanticVisitor semanticVisitor = new SemanticVisitor(parser);
    AST ast = (AST) semanticVisitor.visit(tree);
    semanticErrorListener.printCompilationStatus();
    return ast;
  }

}

