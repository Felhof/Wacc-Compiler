package compiler.visitors;

import antlr.BasicParser.ProgContext;
import antlr.BasicParser.VarDeclarationStatContext;
import antlr.BasicParserBaseVisitor;
import compiler.visitors.identifiers.Identifier;
import compiler.visitors.identifiers.Scalar;
import compiler.visitors.identifiers.Type;
import compiler.visitors.identifiers.Variable;

public class SemanticVisitor extends BasicParserBaseVisitor<Returnable> {

  private static final int ASCII_MAX_VAL = 127; // extended also ?

  // Need top symbol table ?
  private SymbolTable currentST;
  private ASTNode currentASTNode;

  public SemanticVisitor() {
    currentST = new SymbolTable(null);
    // Add basic types to to symbol table
    currentST.add("int", new Scalar(Integer.MIN_VALUE, Integer.MAX_VALUE));
    currentST.add("char", new Scalar(0, ASCII_MAX_VAL));
    currentST.add("bool", new Scalar(0, 1));
  }

  @Override
  public Returnable visitProg(ProgContext ctx) {
    currentASTNode = new ASTNode();
    ctx.func().forEach(this::visitFunc);
    visit(ctx.stat());
    return currentASTNode;
  }

  @Override
  public Returnable visitVarDeclarationStat(VarDeclarationStatContext ctx) {
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
    }

    return null; // what to return ?
  }


}
