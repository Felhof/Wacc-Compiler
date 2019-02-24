package compiler;

import static compiler.instr.REG.LR;
import static compiler.instr.REG.PC;
import static compiler.instr.REG.R0;
import static compiler.instr.REG.R1;
import static compiler.instr.REG.R2;
import static compiler.instr.REG.SP;
import static compiler.visitors.ASTVisitor.addStringField;
import static compiler.visitors.ASTVisitor.toInt;

import compiler.instr.ADD;
import compiler.instr.B;
import compiler.instr.CMP;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.LDR.COND;
import compiler.instr.MOV;
import compiler.instr.Operand.Addr;
import compiler.instr.Operand.Imm_INT;
import compiler.instr.Operand.Imm_STRING_MEM;
import compiler.instr.POP;
import compiler.instr.PUSH;
import java.util.Arrays;
import java.util.List;

public class SubRoutines {

  private static List<Instr> instructions;

  public SubRoutines(List<Instr> instructions) {
    SubRoutines.instructions = instructions;
  }

  public static void addSpecialFunction(String name) {
    switch (name) {
      case "p_print_string":
        addPrintString();
        break;
      case "p_print_int":
        addPrintInt();
        break;
      case "p_print_bool":
        addPrintBool();
        break;
      case "p_print_ln":
        addPrintln();
        break;
      case "p_print_reference":
        addPrintReference();
        break;
      case "p_read_int":
        addReadInt();
        break;
      case "p_read_char":
        addReadChar();
        break;
      case "p_throw_overflow_error":
        addOverflowErr();
        break;
      case "p_throw_runtime_error":
        addRuntimeErr();
        break;
      case "p_check_divide_by_zero":
        addCheckDivideByZero();
        break;
      case "p_check_array_bounds":
        addCheckArrayBounds();
        break;
      case "p_check_null_pointer":
        addNullPointerCheck();
        break;
      case "p_free_pair":
        addFreePairCheck();
        break;
      default:
        break;
    }
  }

  private static void addPrintString() {
    String labelName = addStringField("\"%.*s\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_string"),
      new PUSH(LR),
      new LDR(R1, new Addr(R0), false),
      new ADD(R2, R0, new Imm_INT(toInt("4")), false),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(toInt("4")), false),
      new B("printf", true),
      new MOV(R0, new Imm_INT(toInt("0"))),
      new B("fflush", true),
      new POP(PC)));
  }

  private static void addPrintReference() {
    String labelName = addStringField("\"%p\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_reference"),
      new PUSH(LR),
      new MOV(R1, R0),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(toInt("4")), false),
      new B("printf", true),
      new MOV(R0, new Imm_INT(toInt("0"))),
      new B("fflush", true),
      new POP(PC)));
  }

  private static void addPrintInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_int"),
      new PUSH(LR),
      new MOV(R1, R0),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(4), false)));

    instructions.add(new B("printf", true));

    instructions.addAll(Arrays.asList(
      new MOV(R0, new Imm_INT(toInt("0"))),
      new B("fflush", true),
      new POP(PC)));

  }

  private static void addPrintln() {
    String labelName = addStringField("\"\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_ln"),
      new PUSH(LR),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(toInt("4")), false),
      new B("puts", true),
      new MOV(R0, new Imm_INT(toInt("0"))),
      new B("fflush", true),
      new POP(PC)));
  }

  private static void addPrintBool() {
    String trueLabel = addStringField("\"true\\0\"");
    String falseLabel = addStringField("\"false\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_print_bool"),
      new PUSH(LR),
      new CMP(R0, new Imm_INT(0), null),
      new LDR(R0, new Imm_STRING_MEM(trueLabel), COND.NE),
      new LDR(R0, new Imm_STRING_MEM(falseLabel), COND.EQ),
      new ADD(R0, R0, new Imm_INT(4), false)));

    instructions.add(new B("printf", true));

    instructions.addAll(Arrays.asList(
      new MOV(R0, new Imm_INT(0)),
      new B("fflush", true),
      new POP(PC)
    ));
  }

  private static void addReadInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_read_int"),
      new PUSH(LR),
      new MOV(R1, R0),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(4), false),
      new B("scanf", true),
      new POP(PC)
    ));
  }

  private static void addReadChar() {
    String labelName = addStringField("\" %c\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_read_char"),
      new PUSH(LR),
      new MOV(R1, R0),
      new LDR(R0, new Imm_STRING_MEM(labelName), false),
      new ADD(R0, R0, new Imm_INT(4), false),
      new B("scanf", true),
      new POP(PC)
    ));
  }

  private static void addOverflowErr() {
    String labelName = addStringField(
      "\"OverflowError: the result is too small/large to store in a 4-byte signed-integer."
        + "\\n\"");

    instructions.addAll(Arrays
      .asList(
        new LABEL("p_throw_overflow_error"),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new B("p_throw_runtime_error", true)));

  }

  private static void addRuntimeErr() {

    instructions.addAll(
      Arrays.asList(
        new LABEL("p_throw_runtime_error"),
        new B("p_print_string", true),
        new MOV(R0, new Imm_INT(-1)),
        new B("exit", true)));
  }

  private static void addCheckDivideByZero() {

    String labelName = addStringField(
      "\"DivideByZeroError: divide or modulo by zero\\n\\0\"");

    instructions.addAll(
      Arrays.asList(
        new LABEL("p_check_divide_by_zero"),
        new PUSH(LR),
        new CMP(R1, new Imm_INT(0)),
        new LDR(R0, new Imm_STRING_MEM(labelName), COND.EQ),
        new B("p_throw_runtime_error", true, COND.EQ),
        new POP(PC)));
  }


  private static void addCheckArrayBounds() {
    // todo pass ArrayElem to give index out of bounds used
    String msg0 = addStringField("\"ArrayOutOfBoundError: "
      + "negative index\\n\\0\"");
    String msg1 = addStringField("\"ArrayIndexOutOfBoundsError: "
      + "index too large\\n\\0\"");

    instructions.addAll(Arrays.asList(
      new LABEL("p_check_array_bounds"),
      new PUSH(LR),
      new CMP(R0, new Imm_INT(0)),
      new LDR(R0, new Imm_STRING_MEM(msg0), COND.LT),
      new B("p_throw_runtime_error", true, COND.LT),
      new LDR(R1, new Addr(R1), false),
      new CMP(R0, R1),
      new LDR(R0, new Imm_STRING_MEM(msg1), COND.CS),
      new B("p_throw_runtime_error", true, COND.CS),
      new POP(PC)));
  }

  private static void addNullPointerCheck() {
    String labelName = addStringField(
      "\"NullReferenceError: dereference a null reference\\n\\0\"");

    instructions
      .addAll(Arrays.asList(new LABEL("p_check_null_pointer"), new PUSH(LR),
        new CMP(R0, new Imm_INT(0)),
        new LDR(R0, new Imm_STRING_MEM(labelName), COND.EQ),
        new B("p_throw_runtime_error", true, COND.EQ), new POP(PC)));
  }

  private static void addFreePairCheck() {
    String labelName = addStringField(
      "\"NullReferenceError: dereference a null reference\\n\\0\"");

    instructions
      .addAll(Arrays.asList(new LABEL("p_free_pair"), new PUSH(LR),
        new CMP(R0, new Imm_INT(0)),
        new LDR(R0, new Imm_STRING_MEM(labelName), COND.EQ),
        new B("p_throw_runtime_error", true, COND.EQ), new PUSH(R0),
        new LDR(R0, new Addr(R0), false),
        new B("free", true), new LDR(R0, new Addr(SP), false),
        new LDR(R0, new Addr(R0, true, new Imm_INT(4)), false),
        new B("free", true), new POP(R0), new B("free", true), new POP(PC)));
  }

  private static void addNewBranch() {

  }
}
