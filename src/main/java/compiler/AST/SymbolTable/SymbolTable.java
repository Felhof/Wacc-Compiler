package compiler.AST.SymbolTable;

import compiler.AST.Types.Type;
import compiler.visitors.Returnable;
import java.util.HashMap;

public class SymbolTable {

  private boolean functionScope;
  private SymbolTable encSymTable;
  private HashMap<String, Type> varDict;
  private HashMap<String, FuncTypes> funcDict;

  public SymbolTable(SymbolTable encSymTable) {
    this.encSymTable = encSymTable;
    varDict = new HashMap<>();
    funcDict = new HashMap<>();
    functionScope = false;
  }

  public void addVar(String name, Type ident) {
    varDict.put(name, ident);
  }

  public void addFunc(String name, FuncTypes func) {
    funcDict.put(name, func);
  }

  public Type lookUpAllVar(String name) {
    return (Type) genericLookUpAll(name, "var");
  }

  public FuncTypes lookUpAllFunc(String name) {
    return (FuncTypes) genericLookUpAll(name, "func");
  }

  public Returnable genericLookUpAll(String name, String type) {
    SymbolTable currST = this;
    while (currST != null) {
      Returnable identifier;
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

  public Type lookUpVarScope(String name) {
    return this.varDict.get(name);
  }

  public FuncTypes lookUpFuncScope(String name) {
    return this.funcDict.get(name);
  }

  public SymbolTable getEncSymTable() {
    return encSymTable;
  }

  public boolean isFunctionScope() {
    return functionScope;
  }

  public void setFunctionScope(boolean functionScope) {
    this.functionScope = functionScope;
  }
}
