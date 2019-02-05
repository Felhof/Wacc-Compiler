package invalid.syntax;

import compiler.Main;
import org.junit.Test;

import java.io.*;
import org.junit.experimental.categories.Category;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

interface array {}
interface basic {}
interface expression {}
interface function {}
interface ifI {}
interface sequence {}
interface variables {}
interface whileI {}

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

  @Category(array.class)
  @Test
  public void Array() {
    checkCompilationFails("src/test/java/invalid/syntax/array.txt");
  }

  @Category(basic.class)
  @Test
  public void Basic() {
    checkCompilationFails("src/test/java/invalid/syntax/basic.txt");
  }

  @Category(expression.class)
  @Test
  public void Expression() {
    checkCompilationFails("src/test/java/invalid/syntax/expressions.txt");
  }

  @Category(function.class)
  @Test
  public void Function() {
    checkCompilationFails("src/test/java/invalid/syntax/function.txt");
  }

  @Category(ifI.class)
  @Test
  public void If() {
    checkCompilationFails("src/test/java/invalid/syntax/if.txt");
  }

  @Category(sequence.class)
  @Test
  public void Sequence() {
    checkCompilationFails("src/test/java/invalid/syntax/sequence.txt");
  }

  @Category(variables.class)
  @Test
  public void Variables() {
    checkCompilationFails("src/test/java/invalid/syntax/variables.txt");
  }

  @Category(whileI.class)
  @Test
  public void While() {
    checkCompilationFails("src/test/java/invalid/syntax/while.txt");
  }
}
