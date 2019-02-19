package codegen;

import compiler.AST.Nodes.AST;
import compiler.Main;
import org.junit.Test;

import java.io.*;

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

        return emulator;

      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }

    return null;
  }

  @Test
  public void ExitCodeTest(){

    String path = "src/test/examples/valid/basic/exit/";
    String[] fileNames = { "exit-1","exitBasic", "exitBasic2", "exitWrap"};
    int[] expectedExitCodes = { 255, 7, 42, 0};


    for (int i = 0; i < fileNames.length; i++) {
      String filename = fileNames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);

      Process emulator = assembleAndEmulate(filename);

      assertThat(emulator.exitValue(), is(expectedExitCodes[i]));
    }
  }

  @Test
  public void SkipTest(){
    String path = "src/test/examples/valid/basic/skip/";
    String[] fileNames = {"skip", "comment","skip"};

    for (int i = 0; i < fileNames.length; i++) {
      String filename = fileNames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);

      Process emulator = assembleAndEmulate(filename);

      assertThat(emulator.exitValue(), is(0));
    }
  }

  //Example of how we can test the printed output of a program
  String filename = "printTwoLines";
  String[] expectedLines = new String[]{"True is true", "False is false"};

  @Test
  public void OutputTest(){

    try {

      Process emulator = assembleAndEmulate(filename);

      //Read each line of the output into the sb
      BufferedReader br=new BufferedReader(new InputStreamReader(emulator
              .getInputStream()));

      //StringBuilder sb = new StringBuilder();
      for(String expectedLine : expectedLines){
        String actualLine = br.readLine();
        assertThat(actualLine, is(expectedLine));
      }



    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
