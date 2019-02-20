package compiler.visitors;

import compiler.AST.NodeElements.RHS.IntExpr;
import compiler.AST.NodeElements.RHS.StringExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.AST;
import compiler.AST.Nodes.ExitNode;
import compiler.AST.Nodes.ParentNode;
import compiler.AST.Nodes.PrintNode;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.IntType;
import compiler.instr.*;
import compiler.instr.Operand.Addr;
import compiler.instr.Operand.Imm;
import compiler.instr.Operand.Imm_INT_LDR;
import compiler.instr.BL;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.MOV;
import compiler.instr.Operand.Imm_STRING_LDR;
import compiler.instr.POP;
import compiler.instr.PUSH;
import compiler.instr.REG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static compiler.instr.REG.*;

public class ASTVisitor {
  private List<Instr> instructions;
  private List<Instr> data;
  private List<Instr> functions;
  private Set<String> specialLabels;

  private SymbolTable currentST;
  private List<REG> availableRegs;

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
    this.data = new ArrayList<>();
    this.functions = new ArrayList<>();
    this.specialLabels = new HashSet<>();
    availableRegs = REG.all;
  }

  public List<Instr> generate(AST root) {

    data.add(new SECTION("data"));

    instructions.add(new SECTION("text"));
    instructions.add(new SECTION("main", true));

    instructions.add(new LABEL("main"));
    instructions.add(new PUSH(LR));

    currentST = root.symbolTable();
    visitParentNode(root.root());

    instructions.add(new LDR(R0, new Imm_INT_LDR("0")));  //Cleaning R0 like the
    // reference compiler
    instructions.add(new POP(PC));

    instructions.add(new SECTION("ltorg")); // Assemble directly

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

  public CodeGenData visitUnaryExpr(UnaryExpr expr) {

    if(expr.type().equals(IntType.getInstance()) // Set int value to negative
        && expr.operator() == UNOP.MINUS) {
        ((IntExpr) expr.insideExpr()).setNegative();
    }

    //TODO: handle other types

    return visit(expr.insideExpr());
  }

  public CodeGenData visitIntExpr(IntExpr expr) {
    REG rd = availableRegs.remove(0);
    instructions.add(new LDR(rd, new Imm_INT_LDR(expr.value())));
    return rd;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode){
    REG rd = (REG) visit(printNode.expr());
    // mov result into arg register
    instructions.add(new MOV(R0, rd));
    instructions.add(new BL("p_print_string"));
    specialLabels.add("p_print_string");
    return null;
  }

  public CodeGenData visitStringExpr(StringExpr stringExpr) {
    String labelName = addStringField(stringExpr.getValue());
    REG rd = availableRegs.remove(0);
    instructions.add(new LDR(rd, new Imm_STRING_LDR(labelName)));
    return rd;
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (data.size() - 1);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }

  private void addSpecialFunction(String name){
    switch (name){
      case "p_print_string":
        addPrint();
        break;
    }
  }

  private void addPrint(){
    String labelName = "msg_" + (data.size() / 2);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD("\"%.*s\\0\""));

    functions.addAll(Arrays.asList(
        new LABEL("p_print_string"),
        new PUSH(LR),
        new LDR(R1, new Addr(R0)),
        new ADD(R2, R0, new Imm("4")),
        new LDR(R0, new Imm_STRING_LDR(labelName)),
        new ADD(R0, R0, new Imm("4")),
        new BL("printf"),
        new MOV(R0, new Imm("0")),
        new BL("fflush"),
        new POP(PC)));
  }

}
