package compiler.visitors;

import compiler.visitors.Identifiers.Identifier;
import java.util.HashMap;

public class SymbolTable {

  SymbolTable encSymTable;
  HashMap<String, Identifier> dict;

  public SymbolTable(SymbolTable encSymTable) {
    this.encSymTable = encSymTable;
    dict = new HashMap<>();
  }

  public void add(String name, Identifier ident) {
    dict.put(name, ident);
  }

  public Identifier lookUpAll(String name) {
    SymbolTable currST = this;
    while (currST != null) {
      Identifier ident = currST.lookUpScope(name);
      if (ident != null) {
        return ident;
      }
      currST = currST.getEncSymTable();
    }
    return null;
  }

  public Identifier lookUpScope(String name) {
    return this.dict.get(name);
  }

  public SymbolTable getEncSymTable() {
    return encSymTable;
  }
}
