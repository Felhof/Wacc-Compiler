package compiler.visitors;

import antlr.BasicParser.IfStatContext;
import antlr.BasicParser.ProgContext;
import antlr.BasicParser.RecursiveStatContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.visitors.identifiers.Identifier;
import compiler.visitors.identifiers.Type;
import compiler.visitors.identifiers.Variable;
import java.util.Stack;

public class SemanticVisitor extends BasicParserBaseVisitor<ASTNode> {

  private static final int ASCII_MAX_VAL = 127; // extended also ?

  // Need top symbol table ?
  private SymbolTable currentST;
  private ASTNode currentASTNode;
  private Stack<Identifier> stack;

  public SemanticVisitor() {
    currentST = new SymbolTable(null);
    stack = new Stack<>();

    // Add basic types to to symbol table
//    currentST.add("int", new Scalar(Integer.MIN_VALUE, Integer.MAX_VALUE));
//    currentST.add("char", new Scalar(0, ASCII_MAX_VAL));
//    currentST.add("bool", new Scalar(0, 1));
  }

  @Override
  public ASTNode visitProg(ProgContext ctx) {
    currentASTNode = new ASTNode();
    ctx.func().forEach(this::visitFunc);
    visit(ctx.stat());
    return currentASTNode;
  }

  @Override
  public ASTNode visitRecursiveStat(RecursiveStatContext ctx) {
    visit(ctx.stat(0));
    visit(ctx.stat(1));
    return null;
  }

  @Override
  public ASTNode visitIfStat(IfStatContext ctx) {
    ASTNode parentASTNode = enterScope(); // new ST and AST node

    // Semantic checks
    visit(ctx.expr());
    Identifier conditionalExp = stack.pop(); // put this in AST node
    checkBoolExp(conditionalExp);

    // AST tree

    visit(ctx.stat(0));
    visit(ctx.stat(1));

    exitScope(parentASTNode); // back to enclosing ST and parent AST node
    return null;
  }

  private void checkBoolExp(Identifier pop) {
    // TODO
  }

  private ASTNode enterScope() {
    currentST = new SymbolTable(currentST);
    ASTNode parentASTNode = currentASTNode;
    currentASTNode = new ASTNode();
    return parentASTNode;
  }

  private void exitScope(ASTNode parentASTNode) {
    currentST = currentST.getEncSymTable();
    currentASTNode = parentASTNode;
  }



  @Override
  public ASTNode visitVarDeclarationStat(VarDeclarationStatContext ctx) {
    String typeName = ctx.type().getText();
    String varName = ctx.IDENT().getText();

    Identifier type = currentST.lookUpAll(typeName);
    Identifier var = currentST.lookUpScope(varName);
    // More identifiers ..

    if (type == null) {
      // notify error listener: "unknown type " + typeName
      System.out.println("unknown type " + typeName);
    }
    else if (!(type instanceof Type)) {
      // notify error listener: typeName + " is not a type"
      System.out.println(typeName + " is not a type");
    }
    else if (var != null) { // can be replaced by if but issue with final else
      // notify error listener: varName + " is already declared in scope"
      System.out.println(varName + " is already declared in scope");
    }
    // check assignment ...

    else {
      // legal declaration: add to symbol table
      currentST.add(varName, new Variable((Type) type));

      currentASTNode.add(new ASTNode(/* what to add ? */));
    }

    return null;
  }


}
