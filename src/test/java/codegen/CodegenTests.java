package codegen;

import static compiler.Main.extractFileName;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import compiler.AST.Nodes.AST;
import compiler.Main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.junit.Test;

public class CodegenTests {

  private static final String path = "src/test/java/codegen/paths/";
  private static final String outputFolder = "src/test/java/codegen/output/";

  @Test
  public void ExitCodeTest() {
    compileAndCheckExitAndOutput(path + "exit.txt");
  }

  @Test
  public void SkipTest() {
    compileAndCheckExitAndOutput(path + "skip.txt");
  }

  @Test
  public void SequenceTest() {
    compileAndCheckExitAndOutput(path + "sequence.txt");
  }

  @Test
  public void variablesTest() {
    compileAndCheckExitAndOutput(path + "variables.txt");
  }

  @Test
  public void boolExpressionTest() {
    compileAndCheckExitAndOutput(path + "bool_expressions.txt");
  }

  @Test
  public void integerExpressionTest() {
    compileAndCheckExitAndOutput(path + "int_expressions.txt");
  }

  @Test
  public void charExpressionTest() {
    compileAndCheckExitAndOutput(path + "char_expressions.txt");
  }

  @Test
  public void longExpressionTest() {
    compileAndCheckExitAndOutput(path + "long_expressions.txt");
  }

  @Test
  public void PrintTest() {
    compileAndCheckExitAndOutput(path + "print.txt");
  }

  @Test
  public void PairTest() {
    compileAndCheckExitAndOutput(path + "pair.txt");
  }

  @Test
  public void ArrayTest() {
    compileAndCheckExitAndOutput(path + "arrays.txt");
  }

  @Test
  public void readTest() {
    compileAndCheckExitAndOutput(path + "read.txt");
  }

  @Test
  public void simpleFunctions() {
    compileAndCheckExitAndOutput(path + "simple_functions.txt");
  }

  @Test
  public void simpleRuntimeErr() {
    compileAndCheckExitAndOutput(path + "runtime_errors.txt");
  }

  @Test
  public void scopeTest() {
    compileAndCheckExitAndOutput(path + "scope.txt");
  }

  @Test
  public void whileTest() {
    compileAndCheckExitAndOutput(path + "while.txt");
  }

  @Test
  public void IfSimpleTest() {
    compileAndCheckExitAndOutput(path + "if.txt");
  }

  @Test
  public void functionsTest() {
    //compileAndCheckExitAndOutput(path + "functions.txt");
  }

  @Test
  public void unaryExprTest() {
    compileAndCheckExitAndOutput(path + "unary_expressions.txt");
  }

  public static void compileAndCheckExitAndOutput(String testDataFile) {
    try (BufferedReader br = new BufferedReader(new FileReader(testDataFile))) {
      for (String line; (line = br.readLine()) != null && !line.equals(""); ) {

        String filename;
        String[] expectedOutput = null;
        int expectedExitCode = 0;
        String input = null;

        int expOutputBegin = line.lastIndexOf('{');
        int expOutputEnd = line.lastIndexOf('}');
        if (expOutputBegin > 0) {
          expectedOutput = line.substring(expOutputBegin + 1, expOutputEnd)
              .split(",");
          line = line.replace(line.substring(expOutputBegin - 1,
              expOutputEnd + 1),
              "");
        }

        String[] testData = line.split(" ");
        filename = extractFileName(testData[0]);

        if (testData.length > 1) {
          expectedExitCode = testData[1].equals("_") ? 0 :
              Integer.parseInt(testData[1]);
        }
        if (testData.length == 3) {
          input = testData[2];
        }

        System.out.println("Compiling.. " + filename + ".wacc");
        AST ast = Main.compileProg(testData[0]);
        Main.generateCode(ast, outputFolder + filename);

        System.out.println("Assembling.. " + filename + ".s");
        Process emulator = assembleAndEmulate(outputFolder + filename, input);

        System.out.println("Testing.. " + filename + "\n");
        assertThat(emulator.exitValue(), is(expectedExitCode));
        checkPrintsAreCorrect(emulator, expectedOutput);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void checkPrintsAreCorrect(Process emulator,
      String[] expected) {
    try {
      //Read each line of the output into the sb
      BufferedReader br = new BufferedReader(new InputStreamReader(emulator
          .getInputStream()));

      int i = 0;
      for (String actualLine; (actualLine = br.readLine()) != null; ) {
        if (expected == null || i >= expected.length) {
          fail("Expected output was \"" + actualLine + "\" but actual was "
              + "empty");
        } else {
          assertThat(actualLine, is(expected[i]));
          i++;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Process assembleAndEmulate(String filename, String input) {

    try {
      // Assembler
      Process assembler = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o",
          filename, "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s",
          filename + ".s").start();
      assembler.waitFor();

      // Emulator
      Process emulator = new ProcessBuilder("qemu-arm", "-L", "/usr"
          + "/arm-linux-gnueabi/", filename).start();

      if (input != null) {
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(emulator.getOutputStream()));

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
