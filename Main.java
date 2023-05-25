/** Coder: Karen Page
  * Last development: 05/25/2023
  * Description: This program reads exam/homework scores
  * and reports overall course grade.
  */

// Import files here
import java.util.*;

class Main {
  // Class CONSTANTS go here
  public static final int MAX_ASSESSMENT_POINTS = 100;
  public static final int MAX_SECTION_POINTS = 20;
  public static final int POINTS_PER_SECTION = 3;

  // Custom messages
  private static final String MESSAGE_1 = "Nice work!"; // Grade = 3.0
  private static final String MESSAGE_2 = "That's an okay grade"; // Grade = 2.0
  private static final String MESSAGE_3 = "Make sure you're writing down questions to ask the TA (and are attending study sessions with them)"; // Grade = 0.7
  private static final String MESSAGE_4 = "You need to get your work done!"; // Grade = 0

  public static void main(String[] args) {
    overallGrades();
  }
  // This function is the "main" function, giving all output for the program and controlling which functions are running.
  public static void overallGrades() {
    Scanner console = new Scanner(System.in);
    printHeader();
    double midterm_score = assessment(console, "Midterm");
    double final_score = assessment(console, "Final");
    double homework_score = homework(console);
    console.close();
    double grade = calcGradeAsDecimal(midterm_score, final_score, homework_score);
    printSummary(grade);
  }

  // Print a message describing the purpose of the program
  public static void printHeader() {  
    System.out.println("This program reads exam/homework scores\nand reports your overall course grade.\n");
  }

  // Calculate score for midterm and final, and weighted scores for these tests
  public static double assessment(Scanner console, String assessment) {
    System.out.println(assessment + ":");
    int weight = getWeight(console);
    System.out.print("Score earned? ");
    int assessment_points = console.nextInt();
    System.out.print("Were scores shifted (1=yes, 2=no)? ");
    boolean shift = console.nextInt() == 1;
    if (shift) {
      System.out.print("Shift amount? ");
      assessment_points += console.nextInt();
    }
    assessment_points = Math.min(MAX_ASSESSMENT_POINTS, assessment_points); // Don't allow the points to exceed the maximum
    System.out.printf("Total points = %d / 100\n", assessment_points);
    double unweighted_score = (double) assessment_points / MAX_ASSESSMENT_POINTS;
    double weighted_score = weightedScore(weight, unweighted_score);
    System.out.println();
    return weighted_score;
  }

  // Caclulate score and weighted score for homework category
  public static double homework(Scanner console) {
    System.out.println("Homework:");
    int weight = getWeight(console);

    System.out.print("Number of assignments? ");
    int num_assignments = console.nextInt();

    int homework_points = 0;
    int homework_possible = 0;
    
    for (int i = 1; i <= num_assignments; i++) {
      System.out.print("Assignment " + i + " score and max? ");
      homework_points += console.nextInt();
      homework_possible += console.nextInt();
    }
    homework_points = Math.min(homework_points, homework_possible); // cap points at maximum allowed
  
    System.out.print("How many sections did you attend? ");
    int section_points = console.nextInt() * POINTS_PER_SECTION;
    section_points = Math.min(section_points, MAX_SECTION_POINTS);
    System.out.println("Section points = " + section_points + " / " + MAX_SECTION_POINTS);
    int total_points = homework_points + section_points;
    int total_possible = homework_possible + MAX_SECTION_POINTS;
    System.out.println("Total points = " + total_points + " / " + total_possible);
    double weighted_score = weightedScore(weight, (double) total_points/total_possible);
    System.out.println();
    return weighted_score;
    
  }

  // Overall percentage/grade on 4 point scale for the term
  public static void printSummary(double grade) {
    System.out.printf("Overall percentage = %.1f\n", grade);
    
    double scaled_grade = 0.0;
    String grade_message = MESSAGE_4;
    if (grade > 84.95) {
      scaled_grade = 3.0;
      grade_message = MESSAGE_1;
    } else if (grade > 74.95) {
      scaled_grade = 2.0;
      grade_message = MESSAGE_2;
    } else if (grade >= 59.95) {
      scaled_grade = 0.7;
      grade_message = MESSAGE_3;
    } // grade is already zero if none of these ran.
    System.out.printf("Your grade will be at least: %.1f\n", scaled_grade);
    System.out.println(grade_message);
  }
  // Calculate grade as a decimal
  public static double calcGradeAsDecimal(double midterm_grade, double final_grade, double homework_grade) {
    double grade = midterm_grade + final_grade + homework_grade;
    return grade;
  }

  // Gets weight of category
  public static int getWeight(Scanner console) {
    System.out.print("Weight (0-100)? ");
    int weight = console.nextInt();
    return weight;
  }
  
  // Prints and returns weighted score
  public static double weightedScore(int weight, double score) {
    double weighted_points = score * weight;
    System.out.printf("Weighted score = %.1f / %d\n", weighted_points, weight);
    double weighted_score = score * weight;
    return weighted_score;
  }
}