package compiler;

import compiler.AST.Nodes.AST;
import compiler.instr.Instr;
import compiler.listeners.ErrorListener;
import compiler.visitors.ASTVisitor;
import compiler.visitors.SemanticVisitor;
import compiler.visitors.SyntaxVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import antlr.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    //String path = args[0]; // uncomment for labTS test
    String path = "src/test/examples/valid/array/arrayTest.wacc";
    AST ast = compileProg(path);
    generateCode(ast, extractFileName(path));
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
    ASTVisitor codeGenerator = new ASTVisitor();
    List<Instr> instructions = codeGenerator.generate(ast);

    //instructions.forEach(System.out::println); //test

    String assemblyFile = filename + ".s";
    try {
      // write instructions to assembly file
      PrintWriter writer = new PrintWriter(assemblyFile, String.valueOf(StandardCharsets.UTF_8));

      instructions.forEach(i -> writer.println(i.toString()));

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

