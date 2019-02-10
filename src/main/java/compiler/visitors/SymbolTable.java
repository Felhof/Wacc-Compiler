package compiler.visitors;

import compiler.visitors.Identifiers.Identifier;
import java.util.HashMap;

public class SymbolTable {
  boolean functionScope;
  SymbolTable encSymTable;
  HashMap<String, Identifier> dict;

  public SymbolTable(SymbolTable encSymTable) {
    this.encSymTable = encSymTable;
    dict = new HashMap<>();
    functionScope = false;
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
      if (currST.functionScope) {
        return null;
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

  public boolean isInFunctionScope() {
    SymbolTable currST = this;
    while (currST != null) {
      if (currST.functionScope) {
        return true;
      }
      currST = currST.getEncSymTable();
    }
    return false;
  }

  public void setFunctionScope(boolean functionScope) {
    this.functionScope = functionScope;
  }
}
