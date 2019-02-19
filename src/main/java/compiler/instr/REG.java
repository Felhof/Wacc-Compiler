package compiler.instr;


import compiler.instr.Operand.Operand;
import compiler.visitors.CodeGenData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum REG implements Operand, CodeGenData {
  R0("r0"), R1("r1"), R2("r2"), R3("r3"), R4("r4"), R5("r5"), R6("r6"), R7("r7"), R8("r8"), R9("r9"), R10("r10"), R11("r11"), R12("r12"), SP("sp"), LR("lr") , PC("pc");

  private String s;

  REG(String s) {
    this.s = s;
  }

  public String toString(){
    return s;
  }

  public static final List<REG> all =
      new ArrayList<>(Arrays.asList(R4, R5, R6, R7, R8, R9, R10, R11, R12));
}
