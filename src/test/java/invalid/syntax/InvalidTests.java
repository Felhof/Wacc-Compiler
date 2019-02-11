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

  //Every Error Message begins with this
  private static final String errorMessageToken = "Exit code 100 returned.";

  //The linenumber will be after the first occurence of one of these in the message
  private static final String lineNumberToken1 = "Syntactic Error at ";
  private static final String lineNumberToken2 = "on line ";
  private static final String afterLineNumber1 = ":";
  private static final String afterLineNumber2 = " is ";

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

  public static void checkLinenumberIsCorrect(String filename) {
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

          String theirError = compilerOutput.substring(compilerOutput.indexOf(errorMessageToken) + errorMessageToken.length());
          String OurError = errStream.toString();
          OurError = OurError.substring(OurError.indexOf(errorMessageToken) + errorMessageToken.length());

          System.out.println("\nTheir Error: " + theirError);
          System.out.println("Our Error: " + OurError);

          int lineToken1Index = theirError.indexOf(lineNumberToken1);
          int lineToken2Index = theirError.indexOf(lineNumberToken2);
          int lineNumberIndex;
          String afterLineNumberString;

          assertThat(lineToken1Index >= 0 || lineToken2Index >= 0, is(true));

          if(lineToken1Index >= 0){
            lineNumberIndex = lineToken1Index + lineNumberToken1.length();
            afterLineNumberString = afterLineNumber1;

          }
          else {
            lineNumberIndex = lineToken2Index + lineNumberToken2.length();
            afterLineNumberString = afterLineNumber2;
          }

          int theirErrorLine = Integer.parseInt(theirError.substring(lineNumberIndex,
                                theirError.indexOf(afterLineNumberString)));
          int ourErrorLine = Integer.parseInt(OurError.substring(OurError.indexOf(lineNumberToken1)
                                + lineNumberToken1.length() ,OurError.indexOf(":")));

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
    checkLinenumberIsCorrect("array.txt");
  }

  @Category(basicInv.class)
  @Test
  public void Basic() {
    checkCompilationFails("src/test/java/invalid/syntax/basic.txt");
    checkLinenumberIsCorrect("basic.txt");
  }

  @Category(expressionInv.class)
  @Test
  public void Expression() {
    checkCompilationFails("src/test/java/invalid/syntax/expressions.txt");
    //checkLinenumberIsCorrect("expressions.txt");
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
    checkLinenumberIsCorrect("if.txt");
  }

  @Category(sequenceInv.class)
  @Test
  public void Sequence() {
    checkCompilationFails("src/test/java/invalid/syntax/sequence.txt");
    checkLinenumberIsCorrect("sequence.txt");
  }

  @Category(variablesInv.class)
  @Test
  public void Variables() {
    checkCompilationFails("src/test/java/invalid/syntax/variables.txt");
    //checkLinenumberIsCorrect("variables.txt");
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
    checkLinenumberIsCorrect("pairs.txt");

  }
}
