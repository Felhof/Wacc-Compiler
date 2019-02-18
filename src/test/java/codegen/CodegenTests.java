package codegen;

import compiler.AST.Nodes.AST;
import compiler.Main;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CodegenTests {

  String path = "src/test/examples/valid/basic/exit/";

  String[] names = {"exit-1", "exitBasic", "exitBasic2", "exitWrap"};
  int[] expectedexitCode = {255, 7, 42, 0};

  public void ExitStatus(){

    for(int i = 0; i < names.length; i++) {
      AST ast = Main.compileProg(path + names[i] + ".wacc");

      String file = Main.GenerateCode(names[i], ast);

      try {

        ProcessBuilder pb = new ProcessBuilder("qemu-arm", "-L", "/usr/arm-linux-gnueabi/", file);
        final Process p = pb.start();
        p.waitFor();

        assertThat(p.exitValue(), is(expectedexitCode[i]));

        //This might be usefull when we want the output:
        /*
        BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line=br.readLine())!=null) sb.append(line);

        System.out.println(sb.toString());*/

      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }


  }

}
