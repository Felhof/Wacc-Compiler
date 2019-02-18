package compiler;

import compiler.AST.Nodes.AST;
import compiler.listeners.ErrorListener;
import compiler.visitors.SemanticVisitor;
import compiler.visitors.SyntaxVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import antlr.*;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    AST ast = compileProg(args[0]); // uncomment for labTS test
    generateCode(ast, extractFileName(args[0]));
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

  public static void generateCode(AST ast, String filename){

    String assemblyFile = filename + ".s";

    try {

      PrintWriter writer = new PrintWriter(assemblyFile, StandardCharsets.UTF_8);

      // TODO generate code

//      // Test
//      writer.println(".text");
//      writer.println(".global main");
//      writer.println("main:");
//      writer.println("\tPUSH {lr}");
//      writer.println("\tLDR r4, =7");
//      writer.println("\tMOV r0, r4");
//      writer.println("\tBL exit");
//      writer.println("\tLDR r0, =0");
//      writer.println("\tPOP {pc}");
//      writer.println("\t.ltorg");

      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String extractFileName(String path) {
    int slash = path.lastIndexOf('/');
    int point = path.lastIndexOf('.');
    return path.substring((slash == -1) ? 0 : slash + 1, (point == -1) ?
        path.length() -1 : point);
  }

}

