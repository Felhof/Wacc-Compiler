package valid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import compiler.Main;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

interface basicAndVariables {}
interface ifAndPairs{}
interface sequenceAndScope{}
interface arrayAndWhile{}
interface functions{}
interface expressions{}
interface io{}
interface runtimeErr{}
interface advanced{}

public class ValidTests {

  public static void checkCompilation(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        Main.compileProg(line);
        assertThat(errStream.toString(), is(""));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Category(basicAndVariables.class)
  @Test
  public void BasicAndVariables(){
    checkCompilation("src/test/java/valid/basicAndVariables.txt");
  }

  @Category(ifAndPairs.class)
  @Test
  public void IfAndPairs() {
    checkCompilation("src/test/java/valid/ifAndPairs.txt");
  }

  @Category(sequenceAndScope.class)
  @Test
  public void SequenceAndScope() {
    checkCompilation("src/test/java/valid/sequenceAndScope.txt");
  }

  @Category(arrayAndWhile.class)
  @Test
  public void ArrayAndWhile() {
    checkCompilation("src/test/java/valid/arrayAndWhile.txt");
  }

  @Category(functions.class)
  @Test
  public void Functions() {
    checkCompilation("src/test/java/valid/functions.txt");
  }

  @Category(expressions.class)
  @Test
  public void Expressions() {
    checkCompilation("src/test/java/valid/expressions.txt");
  }

  @Category(io.class)
  @Test
  public void IO() {
    checkCompilation("src/test/java/valid/io.txt");
  }

  @Category(runtimeErr.class)
  @Test
  public void RuntimeErr(){
    checkCompilation("src/test/java/valid/runtimeErr.txt");
  }

  @Category(advanced.class)
  @Test
  public void Advanced() {
    checkCompilation("src/test/java/valid/advanced.txt");
  }
}
