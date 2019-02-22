package compiler.AST.SymbolTable;

import compiler.visitors.ASTData;
import java.util.HashMap;

public class SymbolTable {

  private boolean functionScope;
  private SymbolTable encSymTable;
  private HashMap<String, VarInfo> varDict;
  private HashMap<String, FuncTypes> funcDict;

  public SymbolTable(SymbolTable encSymTable) {
    this.encSymTable = encSymTable;
    varDict = new HashMap<>();
    funcDict = new HashMap<>();
    functionScope = false;
  }

  public void addVar(String name, VarInfo ident) {
    varDict.put(name, ident);
  }

  public void addFunc(String name, FuncTypes func) {
    funcDict.put(name, func);
  }

  public VarInfo lookUpAllVar(String name) {
    return (VarInfo) genericLookUpAll(name, "var");
  }

  public FuncTypes lookUpAllFunc(String name) {
    return (FuncTypes) genericLookUpAll(name, "func");
  }

  public ASTData genericLookUpAll(String name, String type) {
    SymbolTable currST = this;
    while (currST != null) {
      ASTData identifier;
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

  public VarInfo lookUpVarScope(String name) {
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
