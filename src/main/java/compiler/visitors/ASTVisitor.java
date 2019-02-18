package compiler.visitors;

import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.instr.Instr;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor {
  private List<Instr> instructions;
  private SymbolTable currentST;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
  }

  public List<Instr> generate(AST root) {
    currentST = root.symbolTable();

    visitParentNode(root.root());
    return instructions;
  }

  public void visitParentNode(ParentNode node) {

  }



}
