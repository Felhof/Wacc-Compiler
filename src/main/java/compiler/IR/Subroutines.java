package compiler.IR;

import static compiler.AST.Types.Type.WORD_SIZE;
import static compiler.IR.Operand.REG.*;

import compiler.IR.Instructions.*;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Operand.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subroutines {

  private IR program;
  private Set<String> addedSubroutines;

  public Subroutines(IR program) {
    this.program = program;
    program.addData(new SECTION("data"));

    //this.subroutines = new ArrayList<>();
    this.addedSubroutines = new HashSet<>();
  }

  public String addPrintString() {
    String labelName = "p_print_string";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"%.*s\\0\"");

      addStart(labelName);
      program.addAllSubroutines(Arrays.asList(
          new LDR(R1, new Addr(R0)),
          new ADD(R2, R0, new Imm_INT(WORD_SIZE)),
          new LDR(R0, new Imm_STRING_MEM(field))));
      addPrint();
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintReference() {
    String labelName = "p_print_reference";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"%p\\0\"");

      addStart(labelName);
      program.addSubroutines(new MOV(R1, R0));
      program.addSubroutines(new LDR(R0, new Imm_STRING_MEM(field)));
      addPrint();
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintInt() {
    String labelName = "p_print_int";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"%d\\0\"");

      addStart(labelName);
      program.addSubroutines(new MOV(R1, R0));
      program.addSubroutines(new LDR(R0, new Imm_STRING_MEM(field)));
      addPrint();
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintBool() {
    String labelName = "p_print_bool";

    if (!addedSubroutines.contains(labelName)) {
      String trueField = addStringField("\"true\\0\"");
      String falseField = addStringField("\"false\\0\"");

      addStart(labelName);
      program.addAllSubroutines(Arrays.asList(
          new CMP(R0, new Imm_INT(0), null),
          new LDR(R0, new Imm_STRING_MEM(trueField), COND.NE),
          new LDR(R0, new Imm_STRING_MEM(falseField), COND.EQ)));
      addPrint();
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addPrintln() {
    String labelName = "p_print_ln";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"\\0\"");

      addStart(labelName);
      program.addAllSubroutines(Arrays.asList(
          new LDR(R0, new Imm_STRING_MEM(field)),
          new ADD(R0, R0, new Imm_INT(WORD_SIZE)),
          new B("puts", true),
          new MOV(R0, new Imm_INT(0)),
          new B("fflush", true)));
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }



  public String addReadInt() {
    String labelName = "p_read_int";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"%d\\0\"");
      addRead(labelName, field);
      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addReadChar() {
    String labelName = "p_read_char";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\" %c\\0\"");
      addRead(labelName, field);
      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addOverflowErr() {
    String labelName = "p_throw_overflow_error";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String errorMsg = addStringField(
          "\"OverflowError: the result is too small/large to store in a 4-byte signed-integer."
              + "\\n\"");

      program.addAllSubroutines(Arrays.asList(
          new LABEL(labelName),
          new LDR(R0, new Imm_STRING_MEM(errorMsg)),
          new B(runtimeErrLabel, true)));

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addRuntimeErr() {
    // todo add line number
    String labelName = "p_throw_runtime_error";

    if (!addedSubroutines.contains(labelName)) {
      String printStringLabel = addPrintString();

      program.addAllSubroutines(
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
      String errorMsg = addStringField(
          "\"DivideByZeroError: divide or modulo by zero\\n\\0\"");

      addStart(labelName);
      program.addAllSubroutines(
          Arrays.asList(
              new CMP(R1, new Imm_INT(0)),
              new LDR(R0, new Imm_STRING_MEM(errorMsg), COND.EQ),
              new B(runtimeErrLabel, true, COND.EQ)));
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }


  public String addCheckArrayBounds() {
    String labelName = "p_check_array_bounds";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();

      // todo pass ArrayElem to give index out of bounds used
      String errorMsg0 = addStringField("\"ArrayIndexOutOfBoundError: "
          + "negative index\\n\\0\"");
      String errorMsg1 = addStringField("\"ArrayIndexOutOfBoundsError: "
          + "index too large\\n\\0\"");

      addStart(labelName);
      program.addAllSubroutines(Arrays.asList(
          new CMP(R0, new Imm_INT(0)),
          new LDR(R0, new Imm_STRING_MEM(errorMsg0), COND.LT),
          new B(runtimeErrLabel, true, COND.LT),
          new LDR(R1, new Addr(R1)),
          new CMP(R0, R1),
          new LDR(R0, new Imm_STRING_MEM(errorMsg1), COND.CS),
          new B(runtimeErrLabel, true, COND.CS)));
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addNullPointerCheck() {
    String labelName = "p_check_null_pointer";

    if (!addedSubroutines.contains(labelName)) {
      String runtimeErrLabel = addRuntimeErr();
      String errorMsg = addStringField(
          "\"NullReferenceError: dereference a null reference\\n\\0\"");

      addStart(labelName);
      program.addAllSubroutines(
          Arrays.asList(
              new CMP(R0, new Imm_INT(0)),
              new LDR(R0, new Imm_STRING_MEM(errorMsg), COND.EQ),
              new B(runtimeErrLabel, true, COND.EQ)));
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  public String addFreePairCheck() {
    String labelName = "p_free_pair";

    if (!addedSubroutines.contains(labelName)) {
      String nullPointerCheckLabel = addNullPointerCheck();

      addStart(labelName);
      program.addAllSubroutines(Arrays.asList(
              new B(nullPointerCheckLabel, true),
              new PUSH(R0),
              new LDR(R0, new Addr(R0)),
              new B("free", true), new LDR(R0, new Addr(SP)),
              new LDR(R0, new Addr(R0, true, new Imm_INT(WORD_SIZE))),
              new B("free", true), new POP(R0), new B("free", true)));
      addEnd();

      addedSubroutines.add(labelName);
    }
    return labelName;
  }

  private void addStart(String labelName) {
    program.addSubroutines(new LABEL(labelName));
    program.addSubroutines(new PUSH(LR));
  }

  private void addEnd() {
    program.addSubroutines(new POP(PC));
  }

  private void addPrint() {
    program.addAllSubroutines(Arrays.asList(
        new ADD(R0, R0, new Imm_INT(WORD_SIZE)),
        new B("printf", true),
        new MOV(R0, new Imm_INT(0)),
        new B("fflush", true)));
  }

  private void addRead(String labelName, String field) {
    addStart(labelName);
    program.addAllSubroutines(Arrays.asList(
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(field)),
        new ADD(R0, R0, new Imm_INT(WORD_SIZE)),
        new B("scanf", true)));
    addEnd();
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (program.data().size() / 2);
    program.addData(new LABEL(labelName));
    program.addData(new STRING_FIELD(string));
    return labelName;
  }
  
}
