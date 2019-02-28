package frontend.valid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import compiler.Main;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ValidTests {

  String path = "src/test/java/frontend/valid/paths/";

  public static void checkCompilation(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        Main.compileProg(line);
        assertThat(errStream.toString(), is(""));
        new File(Main.extractFileName(line) + ".s").delete();
        new File(Main.extractFileName(line)).delete();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void BasicAndVariables(){
    checkCompilation(path + "basicAndVariables.txt");
  }

  @Test
  public void IfAndPairs() {
    checkCompilation(path + "ifAndPairs.txt");
  }

  @Test
  public void SequenceAndScope() {
    checkCompilation(path + "sequenceAndScope.txt");
  }

  @Test
  public void ArrayAndWhile() {
    checkCompilation(path + "arrayAndWhile.txt");
  }

  @Test
  public void Functions() {
    checkCompilation(path + "functions.txt");
  }

  @Test
  public void Expressions() {
    checkCompilation(path + "expressions.txt");
  }

  @Test
  public void IO() {
    checkCompilation(path + "io.txt");
  }

  @Test
  public void RuntimeErr(){
    checkCompilation(path + "runtimeErr.txt");
  }

  @Test
  public void Advanced() {
    checkCompilation(path + "advanced.txt");
  }
}
