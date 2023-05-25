/* Coder: Karen Page
 * Last development: 05/25/2023
 * Description: Parses a json file and converts it 
 * into an array of tests composed of an input 
 * string and an output string json parsing from
 * this GeeksForGeeks article:
 * https://www.geeksforgeeks.org/parse-json-java/
 * Note: If the tests aren't compiling, type
 * "java -classpath .:target/dependency/* Tests 1"
 * into the shell the first time you open this 
 * program. 
 * If you mess up typing the custom messages, the
 * following will recreate the tests file, and ask
 * for them again:
 * "java -classpath .:target/dependency/* Tests 2"
 */

import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

public class Tests {
  private final String SOURCE_FILE; // User created
  private final String TESTS_FILE; // Created by program, customized version of SOURCE_FILE
  
  private static final String SENTINEL_VAL = "###"; // String used to mark end of custom message (see getCustomMessage function)

  private String[][] test_array; // contains a set of tests of format {input, output}

  public static void main(String[] args) throws FileNotFoundException, IOException {
    new Tests(args[0]);
  }
  
  /* Creates tests
   * Mode 1: generates the test file if it doesn't 
   * already exist
   * Mode 2: Creates a test file regardless of
   * whether one already exists
   * Mode 0: requires an extant test file. If one
   * does not exist, throws an exception. Any
   * character other than 1 or 2 will trigger this
   * mode.
   * Note: Accepts input from System.in if
   * Tests.json has not been created. It seems that 
   * this causes Unit Tests to endlessly wait for
   * user input without supplying a venue to receive
   * it.
   */
  public Tests(String mode) throws FileNotFoundException, IOException {
    SOURCE_FILE = "CleanCopy.json";
    TESTS_FILE = "Tests.json";
    
    JSONObject test_object = new JSONObject();
    if (mode.equals("2")) {
      File f = new File(TESTS_FILE);
      f.createNewFile();
      f.delete();
      customTestBuilder(test_object);
    } else {
      // Check if tests file has been created
      try {
        test_object = fileToJsonObject(TESTS_FILE);
        arrayBuilder(test_object);
      // Creates test file
      } catch (Exception e) { // If one does not exist, create a test file
        if (mode.equals("1")) {
          customTestBuilder(test_object);
        } else {
          throw new FileNotFoundException("Tests.json does not exist. To fix, please call java -classpath .:target/dependency/* Tests 1 in the shell");
        }
      }
    }
  }

  /* @param test_object holds test inputs and outputs. May be empty.
   * Inserts custom messages into tests and writes them to a test file.
   */
  public void customTestBuilder(JSONObject test_object) throws FileNotFoundException, IOException {
    test_object = fileToJsonObject(SOURCE_FILE);
    arrayBuilder(test_object);
    testCustomizer(test_object);
    writeToFile(test_object);
  }

  /* @parameter index is the index of the test to return
   * Returns an array specifying test conditions.
   * test_array may not be initialized
   */
  public String[] getTest(int index) {
    return test_array[index];
  }

  /* @parameter file is a filepath
   * Converts file contents into a JSONObject
   * Returns a JSONObject built from file
   */
  private JSONObject fileToJsonObject(String file) throws FileNotFoundException, IOException {
    JSONObject test_object;
    try {
      test_object = (JSONObject) (new org.json.simple.parser.JSONParser().parse(new FileReader(file)));
    } catch (ParseException e) {
      throw new IllegalArgumentException("File does not correctly parse as a json\n" + e);
    }
    return test_object;
  }

  /* @parameter ob is the JSONObject with the test info
   * Builds an array of tests
   */
  private void arrayBuilder(JSONObject ob) {
    JSONArray json_array = (JSONArray) ob.get("tests");

    test_array = new String[json_array.size()][2];
    
    // Convert JSONObject arrays to String[]
    Object[] temp_array;
    for (int i = 0; i < test_array.length; i++) {
      temp_array = (
        ( (JSONArray) json_array.get(i) ).toArray()
      );

      for (int j = 0; j < temp_array.length; j++) {
        test_array[i][j] = (String) temp_array[j];
      }
    }
  }

  /* Preconditions: initialized test_array
   * @parameter ob contains tests, and customization info
   * Updates ob and test_array with customization
   */
  public void testCustomizer(JSONObject ob) {
    JSONArray json_array = (JSONArray) ob.get("tests");
    
    int num_custom_messages = ((Long) ob.get("num_custom_messages")).intValue();
    String[] custom_messages = getCustomMessages(num_custom_messages);
    JSONArray temp;
    for (int i = 0; i < test_array.length; i++) {
      for (int j = 0; j < test_array[i].length; j++) {
        for (int k = 0; k < num_custom_messages; k++) {
          // Insert custom_messages into test_array
          test_array[i][j] = stringInsertion(test_array[i][j], custom_messages[k], k);
          // insert custom messages into json_array
          temp = ((JSONArray) json_array.get(i));
          temp.set(j, test_array[i][j]);
          json_array.set(i, temp);
        }
      }
    }
    
    // Save work
    ob.put("tests", json_array);
    ob.put("num_custom_messages", 0);
  }

  /* @parameter num_custom_messages is the number of messages to get
   * Gets messages to insert into tests, according to their index
   * Returns an array of messages
   */
  private String[] getCustomMessages(int num_custom_messages) {
    Scanner console = new Scanner(System.in);

    // Build list of custom messages
    String[] custom_messages = new String[num_custom_messages];
    for (int i = 0; i < num_custom_messages; i++) {
      System.out.println("Message #" + (i + 1));
      custom_messages[i] = getCustomMessage(console);
    }
    return custom_messages;
  }

  /*
   * @parameter console is for user input
   * Takes user input to create a custom message
   */
  public String getCustomMessage(Scanner console) {
    String message = "";
    boolean keep_going = true;
    System.out.println("----------");
    String input = "";
    // Keep looking for new lines of the message until user types SENTINEL_VAL
    while (keep_going) {
      System.out.print("Custom_message (type \"" + SENTINEL_VAL + "\") to stop: ");
      input = console.nextLine();
      // Stop
      if (input.equals(SENTINEL_VAL)) {
        keep_going = false;
      // Add new line of input to message
      } else {
        message += input + "\n";
      }
    }
    System.out.println("----------");

    message = message.substring(0, message.length() - 1); // Clear trailing newline character
    return message;
  }

  /* @parameter f_string is a string with lines containing curly braces surrounding insertion indices. These lines are intended for replacement
   * @parameter insertion is the string replacing specified lines in f_string
   * @parameter insertion_index is the number in f_string to replace
   * Inserts custom messages into a string
   * Returns the updated string
   */
  private String stringInsertion(String f_string, String insertion, int insertion_index) {
    String search_string = "{" + insertion_index + "}";
    int search_index = f_string.indexOf(search_string);
    if (search_index != -1) {
      return f_string.substring(0, search_index) + insertion + f_string.substring(search_index + search_string.length());
    }
    else {
      return f_string;
    }
  }

  /* @parameter ob is the object containing test info
   * Saves JSONObject to file specified in TESTS_FILE
   */
  private void writeToFile(JSONObject ob) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(TESTS_FILE);
    pw.write(ob.toJSONString());
    pw.flush();
    pw.close();
  } 
}