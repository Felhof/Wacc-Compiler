package compiler.visitors.backend;

import static compiler.IR.IR.currentId;
import static compiler.IR.Operand.REG.allUsableRegs;

import compiler.AST.ASTData;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.Node;
import compiler.IR.IR;
import compiler.IR.Instructions.SECTION;
import compiler.IR.Operand.REG;
import compiler.IR.Subroutines;
import java.util.ArrayList;
import java.util.List;

// Class responsible of building the IR of the program by redirecting visit
// methods to corresponding the AST visitors
public class ASTVisitor {

  private IR program;
  private static NodeVisitor nodeVisitor;
  private static NodeElemVisitor nodeElemVisitor;

  public ASTVisitor() {
    this.program = new IR();
    Subroutines subroutines = new Subroutines(program);
    List<REG> availableRegs = new ArrayList<>(allUsableRegs);
    nodeVisitor = new NodeVisitor(program, subroutines, availableRegs);
    nodeElemVisitor = new NodeElemVisitor(program, subroutines, availableRegs);
  }

  // Start visiting AST from root and populate IR
  public IR generateCode(AST root) {
    constructStartProgram();
    nodeVisitor.visitFuncsAndChildren(root);
    return program;
  }

  private void constructStartProgram() {
    program.addInstr(new SECTION("text"));
    program.addInstr(new SECTION("main", true));
  }

  // redirect to specific visitor depending if data is a node or a node
  // element of the AST
  static REG visit(ASTData data) {
    if (data instanceof Node) {
      int tempId = currentId;
      currentId++;
      ((Node) data).accept(nodeVisitor);
      currentId = tempId;
    }
    else if (data instanceof NodeElem) {
      return ((NodeElem) data).accept(nodeElemVisitor);
    }
    return null;
  }

}
