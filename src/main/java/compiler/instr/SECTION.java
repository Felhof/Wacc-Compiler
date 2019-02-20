package compiler.instr;

public class SECTION extends Instr {

  private String name;
  private boolean global;

  public SECTION(String name){
    this.name = name;
    this.global = false;
  }

  public SECTION(String name, boolean global) {
    this.name = name;
    this.global = global;
  }

  @Override
  public String toString() {
    return (global ? ".global " : ".") + name;
  }

}
