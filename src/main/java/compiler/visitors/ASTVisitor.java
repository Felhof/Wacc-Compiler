package compiler.visitors;

import static compiler.instr.REG.LR;
import static compiler.instr.REG.PC;
import static compiler.instr.REG.R0;

import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.instr.BL;
import compiler.instr.Imm;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.MOV;
import compiler.instr.POP;
import compiler.instr.PUSH;
import compiler.instr.REG;
import java.util.ArrayList;
import java.util.List;

public class ASTVisitor {
  private List<Instr> instructions;
  private SymbolTable currentST;
  private List<REG> availableRegs;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
  }

  public List<Instr> generate(AST root) {
    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));

    currentST = root.symbolTable();
    availableRegs = REG.all;
    visitParentNode(root.root());

    instructions.add(new LDR(R0, new Imm("0")));  //Cleaning R0 like the reference compiler
    instructions.add(new POP(PC));
    return instructions;
  }

  public void visitParentNode(ParentNode node) {
    node.children().forEach(this::visit);
  }

  private CodeGenData visit(ASTData data) {
    return data.accept(this);
  }

  public void visitExit(ExitNode exitNode) {
    REG rd = (REG) visit(exitNode.exitStatus());
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("exit"));
  }

  public CodeGenData visitIntExpr(IntExpr expr) {
    REG rd = availableRegs.remove(0);
    instructions.add(new LDR(rd, new Imm(expr.value())));
    return rd;
  }

}
