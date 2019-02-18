package compiler.visitors;

import static compiler.instr.Register.R0;
import static compiler.instr.Register.R4;
import static compiler.instr.Register.R5;
import static compiler.instr.Register.R6;
import static compiler.instr.Register.R7;
import static compiler.instr.Register.R8;
import static compiler.instr.Register.R9;
import static compiler.instr.Register.R10;
import static compiler.instr.Register.R11;
import static compiler.instr.Register.R12;
import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.instr.Imm;
import compiler.instr.Instr;
import compiler.instr.MOV;
import compiler.instr.Register;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASTVisitor {
  private List<Instr> instructions;
  private SymbolTable currentST;
  private List<Register> availableRegs;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
  }

  public List<Instr> generate(AST root) {
    currentST = root.symbolTable();
    availableRegs = new ArrayList<>(Arrays.asList(R4, R5, R6, R7, R8, R9, R10, R11, R12));
    visitParentNode(root.root());
    return instructions;
  }

  public void visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
  }

  private CodeGenData visit(ASTData data) {
    return data.accept(this);
  }


  public void visitExit(ExitNode exitNode) {
    Register rd = (Register) visit(exitNode.exitStatus());
    instructions.add(new MOV(R0, rd));
  }

  public CodeGenData transExp(IntExpr expr) {
    Register rd = availableRegs.remove(0);
    instructions.add(new MOV(rd, new Imm(expr.getValue())));
    return rd;
  }
}
