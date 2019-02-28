package compiler.visitors.backend;

import static compiler.IR.Operand.REG.PC;
import static compiler.IR.Operand.REG.R0;
import static compiler.IR.Operand.REG.allUsableRegs;

import compiler.AST.ASTData;
import compiler.AST.NodeElements.NodeElem;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.Node;
import compiler.IR.IR;
import compiler.IR.Instructions.LDR;
import compiler.IR.Instructions.POP;
import compiler.IR.Instructions.SECTION;
import compiler.IR.Operand.Imm_INT_MEM;
import compiler.IR.Operand.REG;
import compiler.IR.Subroutines;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  public IR generate(AST root) {
    constructStartProgram();
    nodeVisitor.visitFuncsAndChildren(root);
    constructEndProgram();
    return program;
  }

  private void constructStartProgram() {
    program.addInstr(new SECTION("text"));
    program.addInstr(new SECTION("main", true));
  }

  private void constructEndProgram() {
    program.addAllInstr(Arrays.asList(
        new LDR(R0, new Imm_INT_MEM(0)),
        new POP(PC),
        new SECTION("ltorg")));
  }

  static REG visit(ASTData data) {
    if (data instanceof Node) {
      ((Node) data).accept(nodeVisitor);
    }
    else if (data instanceof NodeElem) {
      return ((NodeElem) data).accept(nodeElemVisitor);
    }
    return null;
  }

}
