package compiler.visitors.backend;

import compiler.AST.ASTData;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.Node;
import compiler.IR.IR;
import compiler.IR.Operand.REG;

public class ASTVisitor {

  private static NodeVisitor nodeVisitor;
  private static NodeElemVisitor nodeElemVisitor;

  public ASTVisitor() {
    nodeVisitor = new NodeVisitor();
    nodeElemVisitor = new NodeElemVisitor();
  }



  public static REG visit(ASTData data) {
    if (data instanceof Node) {
      ((Node) data).accept(nodeVisitor);
    }
    else if (data instanceof NodeElem) {
      return ((NodeElem) data).accept(nodeElemVisitor);
    }
    return null;
  }

  public IR generate(AST ast) {
    return nodeVisitor.visitAST(ast);
  }
}
