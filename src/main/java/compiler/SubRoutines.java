package compiler;

import static compiler.instr.REG.LR;
import static compiler.instr.REG.PC;
import static compiler.instr.REG.R0;
import static compiler.instr.REG.R1;
import static compiler.instr.REG.R2;
import static compiler.visitors.ASTVisitor.addStringField;
import static compiler.visitors.ASTVisitor.toInt;

import compiler.instr.ADD;
import compiler.instr.BL;
import compiler.instr.CMP;
import compiler.instr.Instr;
import compiler.instr.LABEL;
import compiler.instr.LDR;
import compiler.instr.LDR_COND;
import compiler.instr.LDR_COND.COND;
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
      default:
        break;
    }
  }

  public static void addPrintString() {
    String labelName = addStringField("\"%.*s\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_string"),
        new PUSH(LR),
        new LDR(R1, new Addr(R0), false),
        new ADD(R2, R0, new Imm_INT(toInt("4")), false),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(toInt("4")), false),
        new BL("printf"),
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));
  }

  public static void addPrintReference() {
    String labelName = addStringField("\"%p\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_reference"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(toInt("4")), false),
        new BL("printf"),
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));
  }

  public static void addPrintInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_int"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4), false)));

    instructions.add(new BL("printf"));

    instructions.addAll(Arrays.asList(
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));

  }

  public static void addPrintln() {
    String labelName = addStringField("\"\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_ln"),
        new PUSH(LR),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(toInt("4")), false),
        new BL("puts"),
        new MOV(R0, new Imm_INT(toInt("0"))),
        new BL("fflush"),
        new POP(PC)));
  }

  public static void addPrintBool() {
    String trueLabel = addStringField("\"true\\0\"");
    String falseLabel = addStringField("\"false\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_print_bool"),
        new PUSH(LR),
        new CMP(R0, new Imm_INT(0), null),
        new LDR_COND(R0, new Imm_STRING_MEM(trueLabel), LDR_COND.COND.NE),
        new LDR_COND(R0, new Imm_STRING_MEM(falseLabel), LDR_COND.COND.EQ),
        new ADD(R0, R0, new Imm_INT(4), false)));

    instructions.add(new BL("printf"));

    instructions.addAll(Arrays.asList(
        new MOV(R0, new Imm_INT(0)),
        new BL("fflush"),
        new POP(PC)
    ));
  }

  public static void addReadInt() {
    String labelName = addStringField("\"%d\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_read_int"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4), false),
        new BL("scanf"),
        new POP(PC)
    ));
  }

  public static void addReadChar() {
    String labelName = addStringField("\" %c\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_read_char"),
        new PUSH(LR),
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(labelName), false),
        new ADD(R0, R0, new Imm_INT(4), false),
        new BL("scanf"),
        new POP(PC)
    ));
  }

  public static void addOverflowErr() {
    String labelName = addStringField(
        "\"OverflowError: the result is too small/large to store in a 4-byte signed-integer."
            + "\\n\"");

    instructions.addAll(Arrays
        .asList(
            new LABEL("p_throw_overflow_error"),
            new LDR(R0, new Imm_STRING_MEM(labelName), false),
            new BL("p_throw_runtime_error")));

  }

  public static void addRuntimeErr() {

    instructions.addAll(
        Arrays.asList(
            new LABEL("p_throw_runtime_error"),
            new BL("p_print_string"),
            new MOV(R0, new Imm_INT(-1)),
            new BL("exit")));
  }

  public static void addCheckDivideByZero() {

    String labelName = addStringField(
        "\"DivideByZeroError: divide or modulo by zero\\n\\0\"");

    instructions.addAll(
        Arrays.asList(
            new LABEL("p_check_divide_by_zero"),
            new PUSH(LR),
            new CMP(R1, new Imm_INT(0)),
            new LDR_COND(R0, new Imm_STRING_MEM(labelName), LDR_COND.COND.EQ),
            new BL("p_throw_runtime_error", COND.EQ),
            new POP(PC)));

  }

  public static void addCheckArrayBounds() {
    // todo pass ArrayElem to give index out of bounds used
    String msg0 = addStringField("\"ArrayOutOfBoundError: "
        + "negative index\\n\\0\"");
    String msg1 = addStringField("\"ArrayIndexOutOfBoundsError: "
        + "index too large\\n\\0\"");

    instructions.addAll(Arrays.asList(
        new LABEL("p_check_array_bounds"),
        new PUSH(LR),
        new CMP(R0, new Imm_INT(0)),
        new LDR_COND(R0, new Imm_STRING_MEM(msg0), COND.LT),
        new BL("p_throw_runtime_error", COND.LT),
        new LDR(R1, new Addr(R1), false),
        new CMP(R0, R1),
        new LDR_COND(R0, new Imm_STRING_MEM(msg1), COND.CS),
        new BL("p_throw_runtime_error", COND.CS),
        new POP(PC)));
  }

}
