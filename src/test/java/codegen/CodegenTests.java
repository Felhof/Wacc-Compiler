package codegen;

import compiler.AST.Nodes.AST;
import compiler.Main;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CodegenTests {

  @Test
  public void ExitCodeTest(){

    String path = "src/test/examples/valid/basic/exit/";
    String[] fileNames = { "exitBasic", "exitBasic2", "exitWrap"};
    int[] expectedExitCode = { 7, 42, 0};

    for (int i = 0; i < fileNames.length; i++) {
      String filename = fileNames[i];
      AST ast = Main.compileProg(path + filename + ".wacc");
      Main.generateCode(ast, filename);

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

        assertThat(emulator.exitValue(), is(expectedExitCode[i]));

//        //This might be usefull when we want the output:
//
//        BufferedReader br=new BufferedReader(new InputStreamReader(emulator
//        .getInputStream()));
//        String line;
//        StringBuilder sb = new StringBuilder();
//        while((line=br.readLine())!=null) sb.append(line);
//
//        System.out.println(sb.toString());

      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
