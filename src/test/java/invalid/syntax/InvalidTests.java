package invalid.syntax;

import compiler.Main;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import org.junit.experimental.categories.Category;
import static org.hamcrest.core.Is.is;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

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


  private static final String path = "src/test/java/invalid/syntax/";
  private static final String messageDirectory = "errorMessages/";

  private static final String errorToken = "Syntactic Error at ";

  public static void checkCompilationFails(String filenames) {
    try(BufferedReader br = new BufferedReader(new FileReader(filenames))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        Main.compileProg(line);

        assertThat(errStream.toString().contains("Exit code 100"), is(true));
        assertThat(errStream.toString().contains("Syntactic Error"), is(true));
        assertThat(errStream.toString(), not(""));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void checkCorrectError(String filename) {
    try(BufferedReader br = new BufferedReader(new FileReader(path + filename));
        BufferedReader errorReader = new BufferedReader(new FileReader( path + messageDirectory + filename))) {
      for(String line; (line = br.readLine()) != null; ) {
        System.out.println("Compile.. " + line);
        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        Main.compileProg(line);

        String pathToError = errorReader.readLine();
        //String expectedError = (new BufferedReader(new FileReader(pathToError))).readLine();
        String compilerOutput = new Scanner(new File(pathToError)).useDelimiter("\\A").next();

        try {

          String theirError = compilerOutput.substring(compilerOutput.indexOf(errorToken));
          String OurError = errStream.toString();
          OurError = OurError.substring(OurError.indexOf(errorToken));

          System.out.println("\nTheir Error: " + theirError);
          System.out.println("Our Error: " + OurError);

          int ourErrorLine = Integer.parseInt(theirError.substring(errorToken.length(),theirError.indexOf(":")));
          int theirErrorLine = Integer.parseInt(OurError.substring(errorToken.length(),OurError.indexOf(":")));


          /*
          //Get String after first newline
          String afterFirstNewline = ourError.substring(ourError.indexOf("\n") + 1);

          //index of secondnewline = length until first newline + length from first to second newline + 1
          int secondNewlineIndex = ourError.substring(0, ourError.indexOf("\n")).length() +
                  afterFirstNewline.substring(0, afterFirstNewline.indexOf("\n") + 1).length();

          //Trim message after second newline
          ourError = ourError.substring(0, secondNewlineIndex + 1);
          */


          assertThat(ourErrorLine, is(theirErrorLine));

          System.out.println("Success!\n");

        }
        catch (StringIndexOutOfBoundsException e){
          System.out.println(e.toString());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Category(arrayInv.class)
  @Test
  public void Array() {
    checkCompilationFails("src/test/java/invalid/syntax/array.txt");
    checkCorrectError("array.txt");
  }

  @Category(basicInv.class)
  @Test
  public void Basic() {
    checkCompilationFails("src/test/java/invalid/syntax/basic.txt");
    checkCorrectError("basic.txt");
  }

  @Category(expressionInv.class)
  @Test
  public void Expression() {
    checkCompilationFails("src/test/java/invalid/syntax/expressions.txt");
    checkCorrectError("expressions.txt");
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
    checkCorrectError("if.txt");
  }

  @Category(sequenceInv.class)
  @Test
  public void Sequence() {
    checkCompilationFails("src/test/java/invalid/syntax/sequence.txt");
    checkCorrectError("sequence.txt");
  }

  @Category(variablesInv.class)
  @Test
  public void Variables() {
    checkCompilationFails("src/test/java/invalid/syntax/variables.txt");
    checkCorrectError("variables.txt");
  }

  @Category(whileInv.class)
  @Test
  public void While() {
    checkCompilationFails("src/test/java/invalid/syntax/while.txt");
  }

  @Category(pairInv.class)
  @Test
  public void Pair(){
    checkCompilationFails("src/test/java/invalid/syntax/pairs.txt");
    checkCorrectError("pairs.txt");

  }
}
