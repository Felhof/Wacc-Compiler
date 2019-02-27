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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubRoutines {

  private List<Instr> subroutines;
  private Set<String> addedSubroutines;

  public SubRoutines() {
    this.subroutines = new ArrayList<>();
    this.addedSubroutines = new HashSet<>();
  }

  public List<Instr> getInstructions() {
    return subroutines;
  }

  public String addPrintString() {
    String labelName = "p_print_string";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\"%.*s\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new LDR(R1, new Addr(R0), false),
          new ADD(R2, R0, new Imm_INT(toInt("4")), false),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(toInt("4")), false),
          new B("printf", true),
          new MOV(R0, new Imm_INT(toInt("0"))),
          new B("fflush", true),
          new POP(PC)));
      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintReference() {
    String labelName = "p_print_reference";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\"%p\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new MOV(R1, R0),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(toInt("4")), false),
          new B("printf", true),
          new MOV(R0, new Imm_INT(toInt("0"))),
          new B("fflush", true),
          new POP(PC)));
      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintInt() {
    String labelName = "p_print_int";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\"%d\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new MOV(R1, R0),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(4), false),
          new B("printf", true),
          new MOV(R0, new Imm_INT(toInt("0"))),
          new B("fflush", true),
          new POP(PC)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintln() {
    String labelName = "p_print_ln";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\"\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(toInt("4")), false),
          new B("puts", true),
          new MOV(R0, new Imm_INT(toInt("0"))),
          new B("fflush", true),
          new POP(PC)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintBool() {
    String labelName = "p_print_bool";

    if (!addedSubroutines.contains(labelName)) {
      String trueLabel = addStringField("\"true\\0\"");
      String falseLabel = addStringField("\"false\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new CMP(R0, new Imm_INT(0), null),
          new LDR(R0, new Imm_STRING_MEM(trueLabel), COND.NE),
          new LDR(R0, new Imm_STRING_MEM(falseLabel), COND.EQ),
          new ADD(R0, R0, new Imm_INT(4), false),
          new B("printf", true),
          new MOV(R0, new Imm_INT(0)),
          new B("fflush", true),
          new POP(PC)
      ));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addReadInt() {
    String labelName = "p_read_int";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\"%d\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new MOV(R1, R0),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(4), false),
          new B("scanf", true),
          new POP(PC)
      ));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addReadChar() {
    String labelName = "p_read_char";

    if (!addedSubroutines.contains(labelName)) {
      String msg = addStringField("\" %c\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new MOV(R1, R0),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new ADD(R0, R0, new Imm_INT(4), false),
          new B("scanf", true),
          new POP(PC)
      ));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addOverflowErr() {
    String labelName = "p_throw_overflow_error";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String msg = addStringField(
          "\"OverflowError: the result is too small/large to store in a 4-byte signed-integer."
              + "\\n\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new LDR(R0, new Imm_STRING_MEM(msg), false),
          new B(runtimeErrLabel, true)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addRuntimeErr() {
    String labelName = "p_throw_runtime_error";

    if (!addedSubroutines.contains(labelName)) {
      String printStringLabel = addPrintString();

      subroutines.addAll(
          Arrays.asList(
              new LABEL(labelName),
              new B(printStringLabel, true),
              new MOV(R0, new Imm_INT(-1)),
              new B("exit", true)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addCheckDivideByZero() {
    String labelName = "p_check_divide_by_zero";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String msg = addStringField(
          "\"DivideByZeroError: divide or modulo by zero\\n\\0\"");

      subroutines.addAll(
          Arrays.asList(
              new LABEL(labelName),
              new PUSH(LR),
              new CMP(R1, new Imm_INT(0)),
              new LDR(R0, new Imm_STRING_MEM(msg), COND.EQ),
              new B(runtimeErrLabel, true, COND.EQ),
              new POP(PC)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }


  public String addCheckArrayBounds() {
    String labelName = "p_check_array_bounds";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();

      // todo pass ArrayElem to give index out of bounds used
      String msg0 = addStringField("\"ArrayOutOfBoundError: "
          + "negative index\\n\\0\"");
      String msg1 = addStringField("\"ArrayIndexOutOfBoundsError: "
          + "index too large\\n\\0\"");

      subroutines.addAll(Arrays.asList(
          new LABEL(labelName),
          new PUSH(LR),
          new CMP(R0, new Imm_INT(0)),
          new LDR(R0, new Imm_STRING_MEM(msg0), COND.LT),
          new B(runtimeErrLabel, true, COND.LT),
          new LDR(R1, new Addr(R1), false),
          new CMP(R0, R1),
          new LDR(R0, new Imm_STRING_MEM(msg1), COND.CS),
          new B(runtimeErrLabel, true, COND.CS),
          new POP(PC)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addNullPointerCheck() {
    String labelName = "p_check_null_pointer";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String msg = addStringField(
          "\"NullReferenceError: dereference a null reference\\n\\0\"");

      subroutines
          .addAll(Arrays.asList(new LABEL(labelName), new PUSH(LR),
              new CMP(R0, new Imm_INT(0)),
              new LDR(R0, new Imm_STRING_MEM(msg), COND.EQ),
              new B(runtimeErrLabel, true, COND.EQ), new POP(PC)));
      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addFreePairCheck() {
    String labelName = "p_free_pair";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String msg = addStringField(
          "\"NullReferenceError: dereference a null reference\\n\\0\"");

      subroutines
          .addAll(Arrays.asList(new LABEL(labelName), new PUSH(LR),
              new CMP(R0, new Imm_INT(0)),
              new LDR(R0, new Imm_STRING_MEM(msg), COND.EQ),
              new B(runtimeErrLabel, true, COND.EQ), new PUSH(R0),
              new LDR(R0, new Addr(R0), false),
              new B("free", true), new LDR(R0, new Addr(SP), false),
              new LDR(R0, new Addr(R0, true, new Imm_INT(4)), false),
              new B("free", true), new POP(R0), new B("free", true),
              new POP(PC)));
      addedSubroutines.add(labelName);
    }
    return labelName;
  }
  
}
