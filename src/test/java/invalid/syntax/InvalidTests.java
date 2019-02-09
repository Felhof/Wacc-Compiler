package invalid.syntax;

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

public class InvalidTests {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();


  public void checkCompilationFails(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        exit.expectSystemExitWithStatus(100);
        Main.compileProg(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Category(arrayInv.class)
  @Test
  public void Array() {
    checkCompilationFails("src/test/java/invalid/syntax/array.txt");
  }

  @Category(basicInv.class)
  @Test
  public void Basic() {
    checkCompilationFails("src/test/java/invalid/syntax/basic.txt");
  }

  @Category(expressionInv.class)
  @Test
  public void Expression() {
    checkCompilationFails("src/test/java/invalid/syntax/expressions.txt");
  }

  @Category(functionInv.class)
  @Test
  public void Function() {
    checkCompilationFails("src/test/java/invalid/syntax/function.txt");
  }

  @Category(ifInv.class)
  @Test
  public void If() {
    checkCompilationFails("src/test/java/invalid/syntax/if.txt");
  }

  @Category(sequenceInv.class)
  @Test
  public void Sequence() {
    checkCompilationFails("src/test/java/invalid/syntax/sequence.txt");
  }

  @Category(variablesInv.class)
  @Test
  public void Variables() {
    checkCompilationFails("src/test/java/invalid/syntax/variables.txt");
  }

  @Category(whileInv.class)
  @Test
  public void While() {
    checkCompilationFails("src/test/java/invalid/syntax/while.txt");
  }
}
