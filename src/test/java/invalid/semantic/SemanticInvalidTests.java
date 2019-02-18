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

  String path = "src/test/java/invalid/semantic/paths/";

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
    checkCompilationFails(path + "array.txt");
  }

  @Test
  public void SemanticExit() {
    checkCompilationFails(path + "exit.txt");
  }

  @Test
  public void SemanticExpression() {
    checkCompilationFails(path + "expressions.txt");
  }

  @Test
  public void SemanticFunction() {
    checkCompilationFails(path + "function.txt");
  }

  @Test
  public void SemanticIf() {
    checkCompilationFails(path + "if.txt");
  }

  @Test
  public void SemanticIO() {
    checkCompilationFails(path + "io.txt");
  }

  @Test
  public void SemanticMultiple() {
    checkCompilationFails(path + "multiple.txt");
  }

  @Test
  public void SemanticPairs() {
    checkCompilationFails(path + "pairs.txt");
  }

  @Test
  public void SemanticPrint() {
    checkCompilationFails(path + "print.txt");
  }

  @Test
  public void SemanticRead() {
    checkCompilationFails(path + "read.txt");
  }

  @Test
  public void SemanticScope() {
    checkCompilationFails(path + "scope.txt");
  }

  @Test
  public void SemanticVariables() {
    checkCompilationFails(path + "variables.txt");
  }

  @Test
  public void SemanticWhile() {
    checkCompilationFails(path + "while.txt");
  }

}
