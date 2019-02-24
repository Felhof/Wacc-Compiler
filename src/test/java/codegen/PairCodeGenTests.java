package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class PairCodeGenTests {

  @Test
  public void PairTest() {
    String path = "src/test/examples/valid/pairs/";
    String[] filenames = {"createPair", "createPair02", "createPair03", "writeFst", "writeSnd",
        "printNullPair", "printNull", "null", "createRefPair", "nestedPair", "free"};
    String[][] outputs = {{}, {}, {}, {"10", "42"}, {"a",
        "Z"}, {"(nil)"}, {"(nil)"}, {"(nil)", "(nil)"}, {}, {}, {}};

    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

}
