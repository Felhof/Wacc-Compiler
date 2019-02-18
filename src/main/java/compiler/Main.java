package compiler;

import compiler.AST.Nodes.AST;
import compiler.listeners.ErrorListener;
import compiler.visitors.SemanticVisitor;
import compiler.visitors.SyntaxVisitor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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


  //Returns assembly code for exit-basic for testing purposes
  public static String GenerateCode(String name, AST ast){

    String file = name + ".s";

    try {

      //Generate Assembly Code here
      PrintWriter writer = new PrintWriter(file, "UTF-8");
      writer.println(".text");
      writer.println(".global main");
      writer.println("main:");
      writer.println("\tPUSH {lr}");
      writer.println("\tLDR r4, =7");
      writer.println("\tMOV r0, r4");
      writer.println("\tBL exit");
      writer.println("\tLDR r0, =0");
      writer.println("\tPOP {pc}");
      writer.println("\t.ltorg");
      writer.close();

      //Cross Compile
      Process p = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o", name,
              "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s", file).start();

      p.waitFor();
      System.out.println(p.exitValue());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return name;
  }

}

