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
  public void SemanticArray() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/array.txt");
  }

  @Test
  public void SemanticExit() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/exit.txt");
  }

  @Test
  public void SemanticExpression() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/expressions.txt");
  }

  @Test
  public void SemanticFunction() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/function.txt");
  }

  @Test
  public void SemanticIf() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/if.txt");
  }

  @Test
  public void SemanticIO() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/io.txt");
  }

  @Test
  public void SemanticMultiple() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/multiple.txt");
  }

  @Test
  public void SemanticPairs() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/pairs.txt");
  }

  @Test
  public void SemanticPrint() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/print.txt");
  }

  @Test
  public void SemanticRead() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/read.txt");
  }

  @Test
  public void SemanticScope() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/scope.txt");
  }

  @Test
  public void SemanticVariables() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/variables.txt");
  }

  @Test
  public void SemanticWhile() {
    checkCompilationFails("src/test/java/invalid/semantic/paths/while.txt");
  }

}
