package compiler.visitors;

import compiler.AST.NodeElements.RHS.*;
import compiler.AST.NodeElements.RHS.UnaryExpr.UNOP;
import compiler.AST.Nodes.*;
import compiler.AST.SymbolTable.SymbolTable;
import compiler.AST.Types.BoolType;
import compiler.AST.Types.CharType;
import compiler.AST.Types.IntType;
import compiler.AST.Types.Type;
import compiler.instr.*;
import compiler.instr.Operand.*;
import compiler.instr.BL;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.MOV;
import compiler.instr.POP;
import compiler.instr.PUSH;
import compiler.instr.REG;

import java.util.*;
import java.util.Collections;

import static compiler.instr.REG.*;

public class ASTVisitor {
  private List<Instr> instructions;
  private List<Instr> data;
  private Set<String> specialLabels;

  private SymbolTable currentST;
  private List<REG> availableRegs;

  private Map<Type, String> typeSizes =  Map.of(
          BoolType.getInstance(), "1",
          CharType.getInstance(), "1",
          IntType.getInstance(), "4"
  );

  public ASTVisitor() {
    this.instructions = new ArrayList<>();
    this.data = new ArrayList<>();
    this.specialLabels = new HashSet<>();
    availableRegs = new ArrayList<>(allUsableRegs);
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
    REG rd = useFreeReg();
    instructions.add(new LDR(rd, new Imm_INT_LDR(expr.value())));
    return rd;
  }

  public CodeGenData visitPrintExpression(PrintNode printNode){
    REG rd = (REG) visit(printNode.expr());
    // mov result into arg register
    instructions.add(new MOV(R0, rd));

    if(printNode.expr().type().equals(CharType.getInstance())) {
      jumpToFunctionLabel("putchar");
    }else {
      jumpToFunctionLabel("p_print_string");
      specialLabels.add("p_print_string");
    }

    if(printNode.newLine()) {
      jumpToFunctionLabel("p_print_ln");
      specialLabels.add("p_print_ln");
    }
    return null;
  }

  public CodeGenData visitStringExpr(StringExpr stringExpr) {
    String labelName = addStringField(stringExpr.getValue());
    REG rd = useFreeReg();
    instructions.add(new LDR(rd, new Imm_STRING_LDR(labelName)));
    return rd;
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (data.size() - 1);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }

  public CodeGenData visitCharExpr(CharExpr charExpr) {
    REG rd = useFreeReg();
    instructions.add(new MOV(rd, new Imm_STRING("'" + charExpr.getValue() + "'")));
    return rd;
  }

  public CodeGenData visitBoolExpr(BoolExpr boolExpr){
    REG rd = useFreeReg();
    String value = String.valueOf(boolExpr.value() ? 1 : 0);
    instructions.add(new MOV(rd, new Imm_INT(value)));
    return rd;
  }

  public CodeGenData visitVarDeclareNode(VarDeclareNode varDeclareNode){

    String size = typeSizes.get(varDeclareNode.varType());

    instructions.add(new SUB(SP, SP, new Imm_INT(size)));

    REG rd = (REG) visit(varDeclareNode.rhs());

    instructions.add(new STR(rd, new Addr(SP)));

    instructions.add(new ADD(SP, SP, new Imm_INT(size)));

    return null;
  }

  private void addSpecialFunction(String name){
    switch (name){
      case "p_print_string":
        addPrint();
        break;

      case "p_print_ln":
        addPrintln();
        break;
    }
  }

  private void addPrint(){
    String labelName = addStringField("\"%.*s\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_string"),
        new PUSH(LR),
        new LDR(R1, new Addr(R0)),
        new ADD(R2, R0, new Imm_INT("4")),
        new LDR(R0, new Imm_STRING_LDR(labelName)),
        new ADD(R0, R0, new Imm_INT("4"))));
    jumpToFunctionLabel("printf");
    instructions.addAll(Arrays.asList(
            new MOV(R0, new Imm_INT("0")),
            new BL("fflush"),
            new POP(PC)));

  }

  private void addPrintln(){
    String labelName = addStringField("\"\\0\"");

    instructions.addAll(Arrays.asList(
            new LABEL("p_print_ln"),
            new PUSH(LR),
            new LDR(R0, new Imm_STRING_LDR(labelName)),
            new ADD(R0, R0, new Imm_INT("4")),
            new BL("puts"),
            new MOV(R0, new Imm_INT("0")),
            new BL("fflush"),
            new POP(PC)));
  }

  private void jumpToFunctionLabel(String label) {
    List<REG> usedRegs = getUsedRegs();
    if (!usedRegs.isEmpty()) {
      instructions.add(new PUSH(usedRegs)); // save onto stack all used regs
    }
    instructions.add(new BL(label));
    if (!usedRegs.isEmpty()) {
      Collections.reverse(usedRegs);
      instructions.add(new POP(usedRegs));  // restore previous regs from stack
    }
  }

  private List<REG> getUsedRegs() {
    List<REG> regs = new ArrayList<>(allUsableRegs);
    regs.removeAll(availableRegs);
    return regs;
  }

  private REG useFreeReg() {
    return availableRegs.remove(0);
  }

}
