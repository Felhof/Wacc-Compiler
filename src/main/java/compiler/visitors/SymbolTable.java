package compiler.visitors;

import compiler.visitors.Identifiers.Function;
import compiler.visitors.Identifiers.Identifier;
import compiler.visitors.Identifiers.Variable;
import java.util.HashMap;

public class SymbolTable {
  boolean functionScope;
  SymbolTable encSymTable;
  HashMap<String, Variable> dict;
  HashMap<String, Function> funcDict;

  public SymbolTable(SymbolTable encSymTable) {
    this.encSymTable = encSymTable;
    dict = new HashMap<>();
    funcDict = new HashMap<>();
    functionScope = false;
  }

  public void addVar(String name, Variable ident) {
    dict.put(name, ident);
  }

  public void addFunc(String name, Function func) {funcDict.put(name, func); }

  public Variable lookUpAllVar(String name) {
    return (Variable) genericlookUpAll(name, "var");
  }

  public Function lookUpAllFunc(String name) {
    return (Function) genericlookUpAll(name, "func");
  }

  public Identifier genericlookUpAll(String name, String type) {
    SymbolTable currST = this;
    while (currST != null) {
      Identifier identifier;
      if (type.equals("var")) {
        identifier = currST.lookUpVarScope(name);
      } else {
        identifier = currST.lookUpFuncScope(name);
      }
      if (identifier != null) {
        return identifier;
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

  public Variable lookUpVarScope(String name) {
    return this.dict.get(name);
  }

  public Function lookUpFuncScope(String name) {
    return this.funcDict.get(name);
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
