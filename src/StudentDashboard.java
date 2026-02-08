import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Vector;

/**
 * The StudentDashboard class serves as the main hub for the student.
 */
public class StudentDashboard extends JFrame {
    private String studentName;
    private String department;

    public StudentDashboard(String name) {
        this.studentName = name;
        
        // --- DATA INITIALIZATION ---
        // Fetch the department immediately to use for filtering
        this.department = DatabaseManager.getStudentDepartment(name);

        setTitle("Student Dashboard - " + name);
        setSize(700, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // --- 1. HEADER SECTION ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Welcome, " + name + "!");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(41, 128, 185));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60)); 
        btnLogout.setForeground(Color.WHITE);

        topRow.add(lblWelcome, BorderLayout.WEST);
        topRow.add(btnLogout, BorderLayout.EAST);

        JLabel lblDept = new JLabel("Department: " + department);
        lblDept.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblDept.setForeground(Color.GRAY);

        headerPanel.add(topRow);
        headerPanel.add(lblDept);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTER SECTION: HISTORY TABLE ---
        String[] columns = {"Score", "Total Questions", "Date Taken"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        loadStudentHistory(model); 
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. BOTTOM SECTION: ACTIONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(Color.WHITE);

        JButton btnStart = new JButton("Start Exam");
        JButton btnCourses = new JButton("View My Courses");
        JButton btnProfile = new JButton("Profile Settings");

        styleButton(btnStart, new Color(39, 174, 96));
        styleButton(btnCourses, new Color(52, 152, 219));
        styleButton(btnProfile, new Color(149, 165, 166));

        // --- FIXED ACTION LISTENER ---
        btnStart.addActionListener(e -> {
            // We pass both studentName and department to the updated ExamSystem constructor
            new ExamSystem(studentName, department).setVisible(true);
            dispose();
        });

        btnCourses.addActionListener(e -> showCoursesPopup());
        btnProfile.addActionListener(e -> openProfileSettings());

        btnLogout.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        actionPanel.add(btnCourses);
        actionPanel.add(btnStart);
        actionPanel.add(btnProfile);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }

    private void showCoursesPopup() {
        List<String> courses = DatabaseManager.getCoursesByDepartment(this.department);
        StringBuilder sb = new StringBuilder("Available Courses for " + department + ":\n\n");
        if (courses.isEmpty()) {
            sb.append("No courses found for this department.");
        } else {
            for (String c : courses) {
                sb.append("- ").append(c).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "My Courses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openProfileSettings() {
        JTextField newUserField = new JTextField(studentName);
        JPasswordField newPassField = new JPasswordField();

        Object[] message = {
            "New Username:", newUserField,
            "New Password:", newPassField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newU = newUserField.getText();
            String newP = new String(newPassField.getPassword());

            if (!newU.isEmpty() && !newP.isEmpty()) {
                if (DatabaseManager.updateProfile(studentName, newU, newP)) {
                    JOptionPane.showMessageDialog(this, "Profile updated! Please login again.");
                    new LoginScreen().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Username might be taken.");
                }
            }
        }
    }

    private void loadStudentHistory(DefaultTableModel model) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT score, total, date_taken FROM results WHERE student_name = ? ORDER BY date_taken DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("score"));
                row.add(rs.getInt("total"));
                row.add(rs.getTimestamp("date_taken"));
                model.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}