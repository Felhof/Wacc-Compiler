package compiler.visitors;

import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.instr.*;
import compiler.instr.Operand.Addr;
import compiler.instr.Operand.Imm_LDR;
import compiler.instr.Operand.Imm_MOV;

import java.util.ArrayList;
import java.util.List;

import static compiler.instr.REG.*;

public class ASTVisitor {
  private List<Instr> instructions;
  private List<Instr> data;
  private List<Instr> functions;
  private List<String> specialLabels;

  private SymbolTable currentST;
  private List<REG> availableRegs;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
    this.data = new ArrayList<>();
    this.functions = new ArrayList<>();
    this.specialLabels = new ArrayList<>();
  }

  public List<Instr> generate(AST root) {

    data.add(new FIELD("data"));

    instructions.add(new FIELD("text"));
    instructions.add(new FIELD("main", true));
    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));

    currentST = root.symbolTable();
    availableRegs = REG.all;
    visitParentNode(root.root());

    instructions.add(new LDR(R0, new Imm_LDR("0")));  //Cleaning R0 like the reference compiler
    instructions.add(new POP(PC));
    instructions.add(new FIELD("ltorg"));

    for(String s : specialLabels){
      addSpecialFunction(s);
    }

    data.addAll(instructions);
    data.addAll(functions);
    return data;
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
    instructions.add(new LDR(rd, new Imm_LDR(expr.value())));
    return rd;
  }

  public void visitPrintExpression(String field){
    REG rd = availableRegs.remove(0);
    instructions.add(new LDR(rd, new Imm_LDR(field)));
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("p_print_something"));
    if(!specialLabels.contains("p_print_something")) {
      specialLabels.add("p_print_something");
    }
  }

  public String addString(String ident, boolean newline) {
    String field = "msg_" + (data.size() - 1);
    data.add(new LABEL(field));
    data.add(new FIELD("word " + ident.length()));
    data.add(new FIELD("ascii\t" + ident + (newline ? "\n" : "")));

    return field;
  }

  private void addSpecialFunction(String name){
    switch (name){
      case "p_print_something":
        addPrint();
        break;
    }
  }

  private void addPrint(){

    String field = "msg_" + (data.size() - 1);
    data.add(new LABEL(field));
    data.add(new FIELD("word  5" ));
    data.add(new FIELD("ascii\t\"%.*s\\0\""));


    functions.add(new FIELD("p_print_something"));
    functions.add(new LDR(R1, new Addr("r0")));
    functions.add(new ADD(R2, R0, new Imm_MOV("4")));
    functions.add(new LDR(R0, new Imm_LDR(field)));
    functions.add(new ADD(R0, R0, new Imm_MOV("4")));
    functions.add(new BL("printf"));
    functions.add(new MOV(R0, new Imm_MOV("0")));
    functions.add(new BL("fflush"));
    functions.add(new POP(PC));
  }

}
