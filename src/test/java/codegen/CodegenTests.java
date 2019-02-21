package codegen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import compiler.AST.Nodes.AST;
import compiler.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.junit.Test;

public class CodegenTests {


  private final String outputFolder = "src/test/java/codegen/output/";

  private Process assembleAndEmulate(String filename) {

    try {
      // Assembler
      Process assembler = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o",
          filename, "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s",
          filename + ".s").start();
      assembler.waitFor();

      // Emulator
      Process emulator = new ProcessBuilder("qemu-arm", "-L", "/usr"
          + "/arm-linux-gnueabi/", filename).start();
      emulator.waitFor();

      new File(filename + ".s").delete();
      new File(filename).delete();
      return emulator;
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return null;
    }
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

  @Test
  public void ExitCodeTest() {

    String path = "src/test/examples/valid/basic/exit/";
    String[] filenames = {"exit-1", "exitBasic", "exitBasic2", "exitWrap"};
    int[] expectedExitCodes = {255, 7, 42, 0};

    IntStream.range(0, filenames.length).forEach(i -> {
      String filename = filenames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);
      Process emulator = assembleAndEmulate(outputFolder + filename);
      assertThat(emulator.exitValue(), is(expectedExitCodes[i]));
    });
  }

  @Test
  public void SkipTest() {
    String path = "src/test/examples/valid/basic/skip/";
    String[] filenames = {"skip", "comment", "commentInLine"};

    Arrays.stream(filenames).forEach(filename -> {
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);
      Process emulator = assembleAndEmulate(outputFolder + filename);
      assertThat(emulator.exitValue(), is(0));
    });
  }

  @Test
  public void SequenceTest() {
    String path = "src/test/examples/valid/sequence/";
    String[] simpleFilenames = {"basicSeq", "basicSeq2"};

    // Test that they exit correctly
    Arrays.stream(simpleFilenames).forEach(filename -> {
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);
      Process emulator = assembleAndEmulate(outputFolder + filename);
      assertThat(emulator.exitValue(), is(0));
    });
  }

  @Test
  public void PrintTest() {
    String path = "src/test/examples/valid/IO/print/";
    String[] filenames = {"print", "println", "printChar", "multipleLines"};
    String[][] expectedOutput = {{"Hello World!"},{"Hello World!"},
            {"A simple character example is f"},{"Line1","Line2"}};

    for (int i = 0; i < filenames.length; i++) {
      AST ast = Main.compileProg(path + filenames[i] + ".wacc");
      Main.generateCode(ast, outputFolder + filenames[i]);
      Process emulator = assembleAndEmulate(outputFolder + filenames[i]);
      checkPrintsAreCorrect(emulator, expectedOutput[i]);
    }
  }

  @Test
  public void basicVariableTest(){
    String path = "src/test/examples/valid/variables/";
    String[] filenames = {"boolDeclaration", "boolDeclaration2", "charDeclaration", "charDeclaration2",
            "capCharDeclaration", "intDeclaration", "negIntDeclaration", "zeroIntDeclaration", "manyVariables"};// "puncCharDeclaration"};

    // Test that they exit correctly
    Arrays.stream(filenames).forEach(filename -> {
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);
      Process emulator = assembleAndEmulate(outputFolder + filename);
      assertThat(emulator.exitValue(), is(0));
    });
  }

  @Test
  public void exitWithVar() {
    String path = "src/test/examples/valid/variables/";
    String[] filenames = {"longVarNames", "_VarNames"};
    int[] expectedExitCodes = {5, 19};

    IntStream.range(0, filenames.length).forEach(i -> {
      String filename = filenames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, outputFolder + filename);
      Process emulator = assembleAndEmulate(outputFolder + filename);
      assertThat(emulator.exitValue(), is(expectedExitCodes[i]));
    });
  }


}
