package compiler;

import antlr.BasicLexer;
import antlr.BasicParser;
import compiler.AST.Nodes.AST;
import compiler.IR.IR;
import compiler.listeners.ErrorListener;
import compiler.visitors.backend.ASTVisitor;
import compiler.visitors.backend.CodeGenerator;
import compiler.visitors.frontend.SemanticVisitor;
import compiler.visitors.frontend.SyntaxVisitor;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

  public static void main(String[] args) {
    String path = args[0]; // uncomment for labTS test
    //String path = "src/test/examples/valid/IO/print/assignAndPrint.wacc";
    int exitCode = compileProg(path);
    System.exit(exitCode);
  }

  public static int compileProg(String filename) {
    BasicLexer lexer = lexFile(filename);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    return parser(tokenStream, filename);
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

  public static int parser(CommonTokenStream stream, String filename) {
    BasicParser parser = new BasicParser(stream);

    parser.removeErrorListeners();
    ErrorListener syntaxErrorListener = new ErrorListener("Syntax");
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.prog();

    SyntaxVisitor syntaxVisitor = new SyntaxVisitor(parser);
    syntaxVisitor.visit(tree);
    int exitCode = syntaxErrorListener.printCompilationStatus();
    if (exitCode != 0) {
      return exitCode;
    }
    return semanticCheck(parser,tree, filename);
  }

  public static int semanticCheck(BasicParser parser, ParseTree tree, String filename) {
    parser.removeErrorListeners();
    ErrorListener semanticErrorListener = new ErrorListener("Semantic");
    parser.addErrorListener(semanticErrorListener);
    SemanticVisitor semanticVisitor = new SemanticVisitor(parser);
    AST ast = (AST) semanticVisitor.visit(tree);
    int exitCode = semanticErrorListener.printCompilationStatus();
    if (exitCode != 0) {
      return exitCode;
    }
    return generateCode(ast, extractFileName(filename));
  }

  public static int generateCode(AST ast, String filename){
    ASTVisitor astVisitor = new ASTVisitor();
    IR program = astVisitor.generate(ast);

    String assemblyFile = filename + ".s";
    try {
      // write instructions to assembly file
      PrintWriter writer = new PrintWriter(assemblyFile, String.valueOf(StandardCharsets.UTF_8));
      writer.write(program.print());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static String extractFileName(String path) {
    int slash = path.lastIndexOf('/');
    int point = path.lastIndexOf('.');
    return path.substring((slash == -1) ? 0 : slash + 1, (point == -1) ?
        path.length() -1 : point);
  }
}

