/**
 * The Question class is a "Data Model" or "POJO" (Plain Old Java Object).
 * Its primary purpose is to hold information for a single exam question 
 * after it has been fetched from the database.
 */
public class Question {
    
    // --- FIELDS (Attributes) ---
    // These variables store the raw data for the question.
    // They match the columns found in your 'questions' table in XAMPP.
    String text;      // The actual question being asked
    String optionA;   // Choice A
    String optionB;   // Choice B
    String optionC;   // Choice C
    String optionD;   // Choice D
    String correct;   // Stores the letter (A, B, C, or D) of the right answer

    /**
     * CONSTRUCTOR: This is called when we retrieve data from the Database.
     * It initializes all the fields at once so the object is ready for use in the UI.
     * * @param text    The question content
     * @param a       Text for option A
     * @param b       Text for option B
     * @param c       Text for option C
     * @param d       Text for option D
     * @param correct The correct answer key (e.g., "A")
     */
    public Question(String text, String a, String b, String c, String d, String correct) {
        // 'this' refers to the variables in this class
        this.text = text;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correct = correct;
    }
}