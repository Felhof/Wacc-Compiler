package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class IfCodegenTests {

  @Test
  public void IfSimpleTest() {
    String path = "src/test/examples/valid/if/";
    String[] filenames = {"ifBasic", "ifFalse", "ifTrue", "if1", "if2", "if3",
        "if4", "if5", "if6", "whitespace"};
    String[][] outputs = {{}, {"here"}, {"here"}, {"correct"}, {"correct"},
        {"correct"}, {"correct"}, {"correct"}, {"correct"}, {"1"}};

    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void IfNested() {
    String path = "src/test/examples/valid/scope/";
    String[] filenames = {"ifNested1", "ifNested2"};
    String[][] outputs = {{"correct"}, {"correct"}};

    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

}
