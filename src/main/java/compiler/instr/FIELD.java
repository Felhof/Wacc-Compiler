package compiler.instr;

import java.sql.DataTruncation;

public class FIELD extends Instr {

  private String name;
  private boolean global;

  public FIELD(String name){
    this.name = name;
    this.global = false;
  }

  public FIELD(String name, boolean global) {
    this.name = name;
    this.global = global;
  }

  @Override
  public String toString() {
    return global ? ".global " + name : "." + name;
  }

}
