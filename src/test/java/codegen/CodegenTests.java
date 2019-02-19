package codegen;

import compiler.AST.Nodes.AST;
import compiler.Main;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CodegenTests {

  private Process assembleAndEmulate (String filename) {

      try {

        // Assembler
        Process assembler = new ProcessBuilder("arm-linux-gnueabi-gcc", "-o",
                filename, "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s",
                filename + ".s").start();
        assembler.waitFor();
        //System.out.println(assembler.exitValue());

        // Emulator
        Process emulator = new ProcessBuilder("qemu-arm", "-L", "/usr"
                + "/arm-linux-gnueabi/", filename).start();
        emulator.waitFor();

        new File(filename + ".s").delete();
        new File(filename).delete();

        return emulator;

      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }

    return null;
  }

  private void checkPrintsAreCorrect(Process emulator, String[] expected){
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
  public void ExitCodeTest(){

    String path = "src/test/examples/valid/basic/exit/";
    String[] filenames = { "exit-1","exitBasic", "exitBasic2", "exitWrap"};
    int[] expectedExitCodes = { 255, 7, 42, 0};


    IntStream.range(0, filenames.length).forEach(i -> {
      String filename = filenames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);
      Process emulator = assembleAndEmulate(filename);
      assertThat(emulator.exitValue(), is(expectedExitCodes[i]));
    });
  }

  @Test
  public void SkipTest(){
    String path = "src/test/examples/valid/basic/skip/";
    String[] filenames = {"skip", "comment","skip"};

    Arrays.stream(filenames).forEach(filename -> {
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);
      Process emulator = assembleAndEmulate(filename);
      assertThat(emulator.exitValue(), is(0));
    });
  }

  @Test
  public void SequenceTest(){
    String path = "src/test/examples/valid/sequence/";
    String[] simpleFilenames = {"basicSeq", "basicSeq2"};

    //Simply test that they exit correctly
    Arrays.stream(simpleFilenames).forEach(filename -> {
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);
      Process emulator = assembleAndEmulate(filename);
      assertThat(emulator.exitValue(), is(0));
    });

    String[] filenames = {"exitSimple"};
    String[][] expectedOutput = {{null}}; //exitSimple is not supposed to print anything

    //Test that their output is correct

    for(int i = 0; i < filenames.length; i++){

      Process emulator = assembleAndEmulate(filenames[i]);

      checkPrintsAreCorrect(emulator, expectedOutput[i]);
    }
  }

  //Example of how we can test the printed output of a program
  /*@Test
  public void OutputTest(){

    String filename = "printTwoLines";
    String[] expectedLines = new String[]{"True is true", "False is false"};

    Process emulator = assembleAndEmulate(filename);

    checkPrintsAreCorrect(emulator, expectedLines);
  }*/

}
