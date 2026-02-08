import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Handles all database operations for the Online Exam System.
 */
public class DatabaseManager {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/cbe_system";
    private static final String USER = "root";
    private static final String PASS = ""; // XAMPP default

    // ---------------- CONNECTION ----------------

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ---------------- QUESTIONS (FETCH ALL) ----------------

    /**
     * Fetches every question in the database regardless of department.
     * Fixed the "cannot find symbol" error by adding this method.
     */
    public static List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Question(
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------------- QUESTIONS BY DEPARTMENT (WITH COURSE_ID JOIN) ----------------

    /**
     * Fetches questions filtered by the student's department using the new course_id link.
     */
    public static List<Question> getQuestionsByDepartment(String department) {
        List<Question> list = new ArrayList<>();

        // Using the JOIN to link questions to their courses, then filtering by department
        String sql = "SELECT q.* FROM questions q " +
                     "JOIN courses c ON q.course_id = c.id " +
                     "WHERE c.department = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Question(
                        rs.getString("question_text"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                ));
            }
            
            System.out.println("DEBUG: Found " + list.size() + " questions for " + department);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------------- LOGIN ----------------

    public static boolean loginStudent(String user, String pass) {
        String sql = "SELECT * FROM students WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- STUDENT DEPARTMENT ----------------

    public static String getStudentDepartment(String username) {
        String sql = "SELECT department FROM students WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("department");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "General";
    }

    // ---------------- COURSES BY DEPARTMENT ----------------

    public static List<String> getCoursesByDepartment(String dept) {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT course_name FROM courses WHERE department = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dept);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // ---------------- ADD QUESTION (ADMIN) ----------------

    public static void addQuestion(String text, String a, String b, String c, String d,
                                   String ans, int courseId) {

        String sql = "INSERT INTO questions (question_text, option_a, option_b, " +
                     "option_c, option_d, correct_answer, course_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, text);
            ps.setString(2, a);
            ps.setString(3, b);
            ps.setString(4, c);
            ps.setString(5, d);
            ps.setString(6, ans);
            ps.setInt(7, courseId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- UPDATE PROFILE ----------------

    public static boolean updateProfile(String oldUsername, String newUsername, String newPass) {
        String sql = "UPDATE students SET username = ?, password = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setString(2, newPass);
            ps.setString(3, oldUsername);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- REGISTER STUDENT ----------------

    public static boolean registerStudent(String user, String pass, String dept) {
        String sql = "INSERT INTO students (username, password, department) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, dept);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    // ---------------- SAVE RESULT ----------------

    public static void saveScore(String name, int score, int total) {
        String sql = "INSERT INTO results (student_name, score, total) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, score);
            ps.setInt(3, total);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- ADMIN RESULTS VIEW ----------------

    public static ResultSet getAllResults() throws SQLException {
        Connection conn = getConnection();
        Statement st = conn.createStatement();
        return st.executeQuery("SELECT student_name, score, total, date_taken FROM results");
    }
}