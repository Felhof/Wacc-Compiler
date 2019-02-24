package compiler.instr;

public class SECTION extends Instr {

  private String name;
  private boolean global = false;

  public SECTION(String name){
    this.name = name;
  }

  public SECTION(String name, boolean global) {
    this(name);
    this.global = global;
  }

  @Override
  public String toString() {
    return (global ? ".global " : ".") + name;
  }

}
