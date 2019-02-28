package frontend.invalid.syntax;

import compiler.Main;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.experimental.categories.Category;

interface arrayInv {}
interface basicInv {}
interface expressionInv {}
interface functionInv {}
interface ifInv {}
interface sequenceInv {}
interface variablesInv {}
interface whileInv {}
interface pairInv {}

public class InvalidTests {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  private static final String path = "src/test/java/frontend/invalid/syntax"
      + "/paths/";

  public void checkCompilationFails(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        exit.expectSystemExitWithStatus(100);
        Main.main(new String[]{line});
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Category(arrayInv.class)
  @Test
  public void Array() {
    checkCompilationFails(path +"array.txt");
  }

  @Category(basicInv.class)
  @Test
  public void Basic() {
    checkCompilationFails(path +"basic.txt");
  }

  @Category(expressionInv.class)
  @Test
  public void Expression() {
    checkCompilationFails(path +"expressions.txt");
  }

  @Category(functionInv.class)
  @Test
  public void Function() {
    checkCompilationFails(path +"function.txt");
  }

  @Category(ifInv.class)
  @Test
  public void If() {
    checkCompilationFails(path +"if.txt");
  }

  @Category(sequenceInv.class)
  @Test
  public void Sequence() {
    checkCompilationFails(path +"sequence.txt");
  }

  @Category(variablesInv.class)
  @Test
  public void Variables() {
    checkCompilationFails(path +"variables.txt");
  }

  @Category(whileInv.class)
  @Test
  public void While() {
    checkCompilationFails(path +"while.txt");
  }

  @Category(pairInv.class)
  @Test
  public void Pair(){
    checkCompilationFails(path +"pairs.txt");
  }
}
