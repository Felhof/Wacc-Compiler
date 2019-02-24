package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class ScopeGodeGenTests {

  @Test
  public void newScopeTest() {

    String path = "src/test/examples/valid/scope/";

    String[] filenames = {"scope", "scopeBasic", "scopeRedefine",
        "scopeSimpleRedefine", "scopeVars", "intsAndKeywords"};
    String[][] outputs = {{}, {}, {"true", "2"}, {"true", "12"},
        {"2", "4", "2"}, {}};
    compileAndCheckExitAndOutput(path, filenames, null,
        null, outputs);
  }

}
