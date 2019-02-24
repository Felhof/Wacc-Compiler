package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class Functions {

  @Test
  public void simpleFunctions() {
    String path = "src/test/examples/valid/function/simple_functions/";
    String[] filenames = {"functionDeclaration", "functionSimple",
        "sameArgName", "sameArgName2", "negFunction", "incFunction"}; //};,
        //"functionManyArguments"};
    String[][] outputs = {{}, {}, {"99"}, {"99"}, {"true", "false", "true"},
        {"1", "4"}};/* {"a is 42",
        "b is true", "c is u", "d is hello", "e is 0x22008", "f is 0x22018",
        "answer is g"}}; */
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

}
