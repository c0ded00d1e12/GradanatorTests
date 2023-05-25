/*  Coder: Karen Page
 *  Last developmnet: 05/25/2023
 *  Description: Tests whether output of Gradanator matches
 *  expected output.
 *  
 *  Testing with switching System.in and System.out used
 *  this StackOverflow answer:
 *  https://stackoverflow.com/questions/1647907/junit-how-to-simulate-system-in-testing
 */

import java.io.*;
import java.util.*;
public class GradanatorTest {
  private static final String OUT_PATH = "OutputFile.txt"; // The file Main's output will be saved to

  // Input and output locations
  static InputStream SystemIn = System.in;
  static PrintStream SystemOut = System.out;
  static PrintStream TestOut;

  private int test_num;
  private Tests test_ob;

  private String diffs;

  // Object containing all input output pairs for text
  public GradanatorTest() throws FileNotFoundException, IOException {
    test_ob = new Tests("0");
    this.diffs = "";
  }

  // Runs a test, takes the index of a test in the array
  public boolean test(int test_num, String test_name) throws FileNotFoundException, IOException {
    this.test_num = test_num;
    diffs += test_name + "\n-----\n";
    setIO();
    Main.overallGrades();
    boolean pass = checkRightOutput();
    restoreSysIO();
    diffs += "-----\ntrace info from replit:";
    return pass;
  }

  // Returns information about what went wrong when a test fails
  public String getFailureMessage() {
    return diffs;
  }

  // Setup: don't ask for console input--change System.in and System.out
  private void setIO() throws IOException, FileNotFoundException {
    String test_input = test_ob.getTest(test_num)[0];
    System.setIn(new ByteArrayInputStream(test_input.getBytes()));

    File f = new File(OUT_PATH);
    f.createNewFile();
    TestOut = new PrintStream(f);
    System.setOut(TestOut);
  }
  
  // Cleanup: restore System.in and System.out to normal
  private void restoreSysIO() {
    System.setIn(SystemIn);
    System.setOut(SystemOut);
  }

  // Test logic. Also puts any differences into diffs
  private boolean checkRightOutput() throws FileNotFoundException {
    String expected_output = test_ob.getTest(test_num)[1];
    String test_output = getOutput();
    
    String[] expected_arr = expected_output.split("\n");
    String[] test_arr = test_output.split("\n");
    for (int i = 0; i < (expected_arr.length < test_arr.length ? expected_arr.length : test_arr.length); i++) {
      if (!expected_arr[i].equals(test_arr[i])) {
        diffs += "expected (line " + i + " of output): " + expected_arr[i] + '\n';
        diffs += "received (line " + i + " of output): " + test_arr[i] + '\n';
        diffs += "expected line length (line " + i + " of output): " + expected_arr[i].length() + '\n';
        diffs += "received line length (line " + i + " of output): " + test_arr[i].length() + '\n';
      }
    }
    
    return test_output.equals(expected_output);
  }

  // Makes test output into a string used in the test
  private String getOutput() throws FileNotFoundException {
    File f = new File(OUT_PATH);
    Scanner s = new Scanner(f);
    String output = "";
    boolean read = true;
    while (read) {
      try {
        output += s.nextLine() + '\n';
      } catch (NoSuchElementException e) {
        read = false;
      }
    }
    f.delete();
    
    output = output.substring(0, output.length() - 1);
    return output;
  }
}