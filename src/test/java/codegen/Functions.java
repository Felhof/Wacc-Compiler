package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class Functions {

  @Test
  public void simpleFunctions() {
    String path = "src/test/examples/valid/function/simple_functions/";
    String[] filenames = {"functionDeclaration", "functionSimple",
        "sameArgName", "sameArgName2", "negFunction", "incFunction",
        "functionReturnPair", "functionUpdateParameter"};
    String[][] outputs = {{}, {}, {"99"}, {"99"}, {"true", "false", "true"},
        {"1", "4"}, {"10"}, {"y is 1", "x is 1", "x is now 5", "y is still 1"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void nestedFunctions() {

    String path = "src/test/examples/valid/function/nested_functions/";
    String[] filenames = {"simpleRecursion", "mutualRecursion",
        "functionConditionalReturn", "fibonacciRecursive"};
     //   "fixedPointRealArithmetic"}; //"fibonacciFullRec"};
    String[][] outputs = {{}, {"r1: sending 8",
        "r2: received 8", "r1: sending 7", "r2: received 7", "r1: sending 6",
        "r2: received 6", "r1: sending 5", "r2: received 5", "r1: sending 4",
        "r2: received 4", "r1: sending 3", "r2: received 3", "r1: sending 2",
        "r2: received 2", "r1: sending 1", "r2: received 1"}, {"true"},
        {"The first 20 fibonacci numbers are:",
            "0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181..."}};
        //{"Using fixed-point real: 10 / 3 * 3 = 10"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }
}
