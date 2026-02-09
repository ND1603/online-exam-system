import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

/**
 * Admin dashboard for managing questions and viewing results.
 * Linked to DatabaseManager via course_id.
 */
public class AdminPanel extends JFrame {

    // Map course name → course id for easy database insertion
    private HashMap<String, Integer> courseMap = new HashMap<>();
    private DefaultTableModel resultsModel;

    public AdminPanel() {
        setTitle("CBE Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Add New Question", createAddQuestionPanel());
        tabs.addTab("View Student Results", createResultsPanel());

        add(tabs);
    }

    // ---------------- ADD QUESTION PANEL ----------------

    private JPanel createAddQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));
        panel.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 15));
        form.setBackground(Color.WHITE);

        JTextField txtQ = new JTextField();
        JTextField txtA = new JTextField();
        JTextField txtB = new JTextField();
        JTextField txtC = new JTextField();
        JTextField txtD = new JTextField();

        JComboBox<String> comboAns = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JComboBox<String> comboCourse = new JComboBox<>();

        loadCourses(comboCourse); // fill dropdown from DB

        JButton btnSave = new JButton("Save Question");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        form.add(new JLabel("Question Text:")); form.add(txtQ);
        form.add(new JLabel("Option A:")); form.add(txtA);
        form.add(new JLabel("Option B:")); form.add(txtB);
        form.add(new JLabel("Option C:")); form.add(txtC);
        form.add(new JLabel("Option D:")); form.add(txtD);
        form.add(new JLabel("Correct Answer:")); form.add(comboAns);
        form.add(new JLabel("Course (Linked to Dept):")); form.add(comboCourse);
        form.add(new JLabel("")); form.add(btnSave);

        panel.add(new JLabel("<html><h2 style='color:#2c3e50;'>Manage Question Bank</h2><hr></html>"), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        // ---------- SAVE LOGIC ----------
        btnSave.addActionListener(e -> {
            if (txtQ.getText().trim().isEmpty() || comboCourse.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please fill in the question and select a course.");
                return;
            }

            String courseName = (String) comboCourse.getSelectedItem();
            int courseId = courseMap.get(courseName);

            DatabaseManager.addQuestion(
                    txtQ.getText().trim(),
                    txtA.getText().trim(),
                    txtB.getText().trim(),
                    txtC.getText().trim(),
                    txtD.getText().trim(),
                    comboAns.getSelectedItem().toString(),
                    courseId
            );

            JOptionPane.showMessageDialog(this, "Question successfully linked to " + courseName);

            // Clear fields
            txtQ.setText(""); txtA.setText(""); txtB.setText(""); 
            txtC.setText(""); txtD.setText("");
        });

        return panel;
    }

    // ---------------- LOAD COURSES ----------------

    private void loadCourses(JComboBox<String> combo) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, course_name FROM courses")) {

            combo.removeAllItems();
            while (rs.next()) {
                String name = rs.getString("course_name");
                int id = rs.getInt("id");
                combo.addItem(name);
                courseMap.put(name, id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: Could not load courses.");
        }
    }

    // ---------------- RESULTS PANEL ----------------

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columns = {"Student Name", "Score", "Total", "Date Taken"};
        resultsModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(resultsModel);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.addActionListener(e -> refreshResults());

        refreshResults(); // Initial load

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("<html><h2 style='color:#2c3e50;'>Student Performance Report</h2></html>"), BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void refreshResults() {
        resultsModel.setRowCount(0); // Clear old data
        try (ResultSet rs = DatabaseManager.getAllResults()) {
            while (rs.next()) {
                resultsModel.addRow(new Object[]{
                        rs.getString("student_name"),
                        rs.getInt("score"),
                        rs.getInt("total"),
                        rs.getTimestamp("date_taken")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}