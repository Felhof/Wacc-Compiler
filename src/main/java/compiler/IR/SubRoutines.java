package compiler.IR;

import static compiler.AST.Types.Type.WORD_SIZE;
import static compiler.IR.Operand.REG.LR;
import static compiler.IR.Operand.REG.PC;
import static compiler.IR.Operand.REG.R0;
import static compiler.IR.Operand.REG.R1;
import static compiler.IR.Operand.REG.R2;
import static compiler.IR.Operand.REG.SP;

import compiler.IR.Instructions.ADD;
import compiler.IR.Instructions.B;
import compiler.IR.Instructions.CMP;
import compiler.IR.Instructions.Instr;
import compiler.IR.Instructions.LABEL;
import compiler.IR.Instructions.LDR;
import compiler.IR.Instructions.LDR.COND;
import compiler.IR.Instructions.MOV;
import compiler.IR.Operand.Addr;
import compiler.IR.Operand.Imm_INT;
import compiler.IR.Operand.Imm_STRING_MEM;
import compiler.IR.Instructions.POP;
import compiler.IR.Instructions.PUSH;
import compiler.IR.Instructions.SECTION;
import compiler.IR.Instructions.STRING_FIELD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubRoutines {

  private List<Instr> data;         // list of data fields on top
  private List<Instr> subroutines;  // list of common labels at the end
  private Set<String> addedSubroutines;

  public SubRoutines() {
    this.data = new ArrayList<>();
    data.add(new SECTION("data"));

    this.subroutines = new ArrayList<>();
    this.addedSubroutines = new HashSet<>();
  }

  public List<Instr> getDataFields() { return data; }

  public List<Instr> getInstructions() {
    return subroutines;
  }

  public String addPrintString() {
    String labelName = "p_print_string";

    if (!addedSubroutines.contains(labelName)) {
      String field = addStringField("\"%.*s\\0\"");

      addStart(labelName);
      subroutines.addAll(Arrays.asList(
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
      subroutines.add(new MOV(R1, R0));
      subroutines.add(new LDR(R0, new Imm_STRING_MEM(field)));
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
      subroutines.add(new MOV(R1, R0));
      subroutines.add(new LDR(R0, new Imm_STRING_MEM(field)));
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
      subroutines.addAll(Arrays.asList(
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
      subroutines.addAll(Arrays.asList(
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

      subroutines.addAll(Arrays.asList(
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
      String errorMsg = addStringField(
          "\"DivideByZeroError: divide or modulo by zero\\n\\0\"");

      addStart(labelName);
      subroutines.addAll(
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
      subroutines.addAll(Arrays.asList(
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
      subroutines
          .addAll(Arrays.asList(
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
      subroutines.addAll(Arrays.asList(
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
    subroutines.add(new LABEL(labelName));
    subroutines.add(new PUSH(LR));
  }

  private void addEnd() {
    subroutines.add(new POP(PC));
  }

  private void addPrint() {
    subroutines.addAll(Arrays.asList(
        new ADD(R0, R0, new Imm_INT(WORD_SIZE)),
        new B("printf", true),
        new MOV(R0, new Imm_INT(0)),
        new B("fflush", true)));
  }

  private void addRead(String labelName, String field) {
    addStart(labelName);
    subroutines.addAll(Arrays.asList(
        new MOV(R1, R0),
        new LDR(R0, new Imm_STRING_MEM(field)),
        new ADD(R0, R0, new Imm_INT(WORD_SIZE)),
        new B("scanf", true)));
    addEnd();
  }

  public String addStringField(String string) {
    String labelName = "msg_" + (data.size() / 2);
    data.add(new LABEL(labelName));
    data.add(new STRING_FIELD(string));
    return labelName;
  }
  
}
