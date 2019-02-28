package frontend.invalid.syntax;

import compiler.Main;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class AllInvalidSyntacticTests {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Parameter
  public String file;

  public static String path = "src/test/java/frontend/invalid/syntax/paths"
      + "/all.txt";

  @Parameters(name = "{index}: {0}")
  public static List<String> data() {
    List<String> filenames = new ArrayList<>();
    try(BufferedReader br = new BufferedReader(new FileReader(path))) {
      for(String line; (line = br.readLine()) != null; ) {
        filenames.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return filenames;
  }

  @org.junit.Test
  public void Test() {
    checkCompilationFails(file);
  }

  public void checkCompilationFails(String filename) {
    System.out.println("Compile.. " + filename);
    final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errStream));
    exit.expectSystemExitWithStatus(100);
    Main.main(new String[]{filename});
  }

}
