package invalid.semantic;

import compiler.Main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class SemanticInvalidTests {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  public void checkCompilationFails(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        exit.expectSystemExitWithStatus(200);
        Main.compileProg(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void Exit() {
    checkCompilationFails("src/test/java/invalid/semantic/exit.txt");
  }

  @Test
  public void Expression() {
    checkCompilationFails("src/test/java/invalid/semantic/expressions.txt");
  }

  @Test
  public void Function() {
    checkCompilationFails("src/test/java/invalid/semantic/function.txt");
  }

  @Test
  public void If() {
    checkCompilationFails("src/test/java/invalid/semantic/if.txt");
  }

  @Test
  public void IO() {
    checkCompilationFails("src/test/java/invalid/semantic/io.txt");
  }

  @Test
  public void Multiple() {
    checkCompilationFails("src/test/java/invalid/semantic/multiple.txt");
  }

  @Test
  public void Pairs() {
    checkCompilationFails("src/test/java/invalid/semantic/pairs.txt");
  }

  @Test
  public void Print() {
    checkCompilationFails("src/test/java/invalid/semantic/print.txt");
  }

  @Test
  public void Read() {
    checkCompilationFails("src/test/java/invalid/semantic/read.txt");
  }

  @Test
  public void Scope() {
    checkCompilationFails("src/test/java/invalid/semantic/scope.txt");
  }

  @Test
  public void Variables() {
    checkCompilationFails("src/test/java/invalid/semantic/variables.txt");
  }

  @Test
  public void While() {
    checkCompilationFails("src/test/java/invalid/semantic/while.txt");
  }

}
