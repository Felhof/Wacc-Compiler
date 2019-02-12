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
    return (Variable) genericLookUpAll(name, "var");
  }

  public Function lookUpAllFunc(String name) {
    return (Function) genericLookUpAll(name, "func");
  }

  public Identifier genericLookUpAll(String name, String type) {
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

  public void setFunctionScope(boolean functionScope) {
    this.functionScope = functionScope;
  }

  public void setDict(
      HashMap<String, Variable> dict) {
    this.dict = dict;
  }

  public boolean functionScope() {
    return functionScope;
  }
}
