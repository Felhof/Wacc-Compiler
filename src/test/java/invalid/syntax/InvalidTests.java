package invalid.syntax;

import compiler.Main;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

interface BasicAndVariables {}

public class InvalidTests {


  public static void checkCompilationFails(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        Main.compileProg(line);
        assertThat(errStream.toString(), not(""));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void array() {
    checkCompilationFails("src/test/java/invalid/syntax/array.txt");
  }

  @Test
  public void basic() {
    checkCompilationFails("src/test/java/invalid/syntax/basic.txt");
  }

  @Test
  public void expression() {
    checkCompilationFails("src/test/java/invalid/syntax/expressions.txt");
  }

  @Test
  public void function() {
    checkCompilationFails("src/test/java/invalid/syntax/function.txt");
  }

  @Test
  public void ifs() {
    checkCompilationFails("src/test/java/invalid/syntax/if.txt");
  }

  @Test
  public void sequence() {
    checkCompilationFails("src/test/java/invalid/syntax/sequence.txt");
  }

  @Test
  public void variables() {
    checkCompilationFails("src/test/java/invalid/syntax/variables.txt");
  }

  @Test
  public void whiles() {
    checkCompilationFails("src/test/java/invalid/syntax/while.txt");
  }


}
