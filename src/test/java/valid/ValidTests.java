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

interface BasicAndVariables{}

public class ValidTests {

  @Category(BasicAndVariables.class)
  @Test
  public void BasicAndVariables(){
    try(BufferedReader br = new BufferedReader(new FileReader("src/test/java/valid/basicAndVariables.txt"))) {
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


}
