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

interface basicAndVariablesVal {}
interface ifAndPairsVal{}
interface sequenceAndScopeVal{}
interface arrayAndWhileVal{}
interface functionsVal{}
interface expressionsVal{}
interface ioVal{}
interface runtimeErrVal{}
interface advancedVal{}

public class ValidTests {

  String path = "src/test/java/valid/paths/";

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

  @Category(basicAndVariablesVal.class)
  @Test
  public void BasicAndVariables(){
    checkCompilation(path + "basicAndVariables.txt");
  }

  @Category(ifAndPairsVal.class)
  @Test
  public void IfAndPairs() {
    checkCompilation(path + "ifAndPairs.txt");
  }

  @Category(sequenceAndScopeVal.class)
  @Test
  public void SequenceAndScope() {
    checkCompilation(path + "sequenceAndScope.txt");
  }

  @Category(arrayAndWhileVal.class)
  @Test
  public void ArrayAndWhile() {
    checkCompilation(path + "arrayAndWhile.txt");
  }

  @Category(functionsVal.class)
  @Test
  public void Functions() {
    checkCompilation(path + "functions.txt");
  }

  @Category(expressionsVal.class)
  @Test
  public void Expressions() {
    checkCompilation(path + "expressions.txt");
  }

  @Category(ioVal.class)
  @Test
  public void IO() {
    checkCompilation(path + "io.txt");
  }

  @Category(runtimeErrVal.class)
  @Test
  public void RuntimeErr(){
    checkCompilation(path + "runtimeErr.txt");
  }

  @Category(advancedVal.class)
  @Test
  public void Advanced() {
    checkCompilation(path + "advanced.txt");
  }
}
