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


  @Test
  public void arrayNested() {
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"arrayNested"};
    String[][] outputs = {{"3", "3"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }
/*
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

  @Test
  public void arrayPrint() {
    // todo implement if and while
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"arrayPrint"};
    String[][] outputs = {{"0x22008 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }

  @Test
  public void array() {
    // todo implement if and while
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"array"};
    String[][] outputs = {{"0x22008 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }*/

  /*
    @Test
  public void printArrayRef() {
    // different address but should be ok
    String path = "src/test/examples/valid/array/";
    String[] filenames = {"printRef"};
    String[][] outputs =
        {{"Printing an array variable gives an address, such as 0x23010"}};
    compileAndCheckExitAndOutput(path, filenames, null, null, outputs);
  }
  */

}
