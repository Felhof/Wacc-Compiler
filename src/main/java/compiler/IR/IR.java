package compiler.IR;

import compiler.IR.Instructions.Instr;
import java.util.ArrayList;
import java.util.List;

public class IR {

  private List<Instr> data;         // list of data fields on top
  private List<Instr> instructions; // list of ARM instructions
  private List<Instr> subroutines;  // list of common labels at the end
  public static int currentId;

  public IR() {
    this.data = new ArrayList<>();
    this.instructions = new ArrayList<>();
    this.subroutines = new ArrayList<>();
    currentId = 0;
  }

  public void addData(Instr instr) {
    data.add(instr);
  }

  public void addInstr(Instr instr) {
    instructions.add(instr);
  }

  public void addAllInstr(List<Instr> instrs) {
    instructions.addAll(instrs);
  }

  public void addSubroutines(Instr instr) {
    subroutines.add(instr);
  }

  public void addAllSubroutines(List<Instr> instrs) {
    subroutines.addAll(instrs);
  }

  public List<Instr> data() {
    return data;
  }

  public String print() { // Converts internal representation into a string
    StringBuilder sb = new StringBuilder();
    data.forEach(i -> sb.append(i.toString()).append('\n'));
    instructions.forEach(i -> sb.append(i.toString()).append('\n'));
    subroutines.forEach(i -> sb.append(i.toString()).append('\n'));
    return sb.toString();
  }
}
