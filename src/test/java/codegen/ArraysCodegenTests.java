package codegen;

import static codegen.CodegenTests.compileAndCheckExitAndOutput;

import org.junit.Test;

public class ArraysCodegenTests {

  @Test
  public void declareAssignAndPrintBasic() {
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"arraySimple", "arrayLookup", "arrayEmpty"};
    String[][] outputs = {{"42"}, {"43"}, {}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }
  /*
  @Test
  public void arrayNested() {
    // todo implement nested array case
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"arrayNested"};
    String[][] outputs = {{"3\n3"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void printArrayRef() {
    // todo not same address possible ?
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"printRef"};
    String[][] outputs =
        {{"Printing an array variable gives an address, such as 0x23010"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void stringAsArray() {
    // todo implement string as array behaviour
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"modifyString"};
    String[][] outputs =
        {{"hello world!\nHello world!\nHi!"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void arrayLength() {
    // todo implement unary op 'len'
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"arrayLength"};
    String[][] outputs =
        {{"4"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }
  */
}
