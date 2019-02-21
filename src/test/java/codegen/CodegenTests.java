package codegen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import compiler.AST.Nodes.AST;
import compiler.Main;

import java.io.*;
import java.util.stream.IntStream;
import org.junit.Test;

public class CodegenTests {

  private final String outputFolder = "src/test/java/codegen/output/";

  @Test
  public void ExitCodeTest() {
    String path = "src/test/examples/valid/basic/exit/";
    String[] filenames = {"exit-1", "exitBasic", "exitBasic2", "exitWrap"};
    int[] expectedExitCodes = {255, 7, 42, 0};

    compileAndCheckExitAndOutput(path, filenames, null, expectedExitCodes, null);
  }

  @Test
  public void SkipTest() {
    String path = "src/test/examples/valid/basic/skip/";
    String[] filenames = {"skip", "comment", "commentInLine"};

    compileAndCheckExitAndOutput(path, filenames, null, null, null);
  }

  @Test
  public void SequenceTest() {
    String path = "src/test/examples/valid/sequence/";
    String[] simpleFilenames = {"basicSeq", "basicSeq2"};

    compileAndCheckExitAndOutput(path, simpleFilenames, null, null, null);
  }

  @Test
  public void basicVariableTest(){
    String path = "src/test/examples/valid/variables/";
    String[] filenames = {"boolDeclaration", "boolDeclaration2", "charDeclaration", "charDeclaration2",
        "capCharDeclaration", "intDeclaration", "negIntDeclaration", "zeroIntDeclaration", "manyVariables"};// "puncCharDeclaration"};

    compileAndCheckExitAndOutput(path, filenames, null, null, null);
  }

  @Test
  public void exitWithVar() {
    String path = "src/test/examples/valid/variables/";
    String[] filenames = {"longVarNames", "_VarNames"};
    int[] expectedExitCodes = {5, 19};

    compileAndCheckExitAndOutput(path, filenames, null, expectedExitCodes, null);
  }

  @Test
  public void PrintTest() {
    String path = "src/test/examples/valid/IO/print/";
    String[] filenames = {"print", "println", "printChar", "multipleLines"};
    String[][] expectedOutput = {{"Hello World!"},{"Hello World!"},
            {"A simple character example is f"},{"Line1","Line2"}};

    compileAndCheckExitAndOutput(path, filenames, null, null, expectedOutput);
  }

  @Test
  public void PairTest() {
    String path = "src/test/examples/valid/pairs/";
    String[] filenames = {"createPair", "createPair02", "createPair03"};

    compileAndCheckExitAndOutput(path, filenames, null,null, null);
  }

  @Test
  public void readTest() {
    String path = "src/test/examples/valid/IO/read/";
    String[] filenames = { "readAndPrint"};
    String[] inputs = { "c"};
    String[][] outputs = { {"input a character to continue...", "c"}};

    compileAndCheckExitAndOutput(path, filenames, inputs, null, outputs);
  }


  // provide path, filenames, exit codes, and expected output
  private void compileAndCheckExitAndOutput(String path, String[] filenames, String[] inputs,
      int[] expectedExitCodes, String [][] expectedOutput) {
    IntStream.range(0, filenames.length).forEach(i -> {
      String filename = filenames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);

      Process emulator;
      if(inputs != null) {
        emulator = assembleAndEmulate(outputFolder + filename, inputs[i]);
      }
      else {
        emulator = assembleAndEmulate(outputFolder + filename, null);
      }

      if (expectedExitCodes == null) {
        assertThat(emulator.exitValue(), is(0));
      } else {
        assertThat(emulator.exitValue(), is(expectedExitCodes[i]));
      }
      if (expectedOutput != null) {
        checkPrintsAreCorrect(emulator, expectedOutput[i]);
      }
    });
  }

  private void checkPrintsAreCorrect(Process emulator, String[] expected) {
    try {
      //Read each line of the output into the sb
      BufferedReader br = new BufferedReader(new InputStreamReader(emulator
          .getInputStream()));

      for (String expectedLine : expected) {
        String actualLine = br.readLine();
        assertThat(actualLine, is(expectedLine));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Process assembleAndEmulate(String filename, String input) {

    try {
      // Assembler
      Process assembler = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o",
          filename, "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s",
          filename + ".s").start();
      assembler.waitFor();

      // Emulator
      Process emulator = new ProcessBuilder("qemu-arm", "-L", "/usr"
          + "/arm-linux-gnueabi/", filename).start();

      if(input != null){
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(emulator.getOutputStream()));

        writer.write(input);
        writer.flush();
      }

      emulator.waitFor();

      new File(filename + ".s").delete();
      new File(filename).delete();
      return emulator;
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }
}
