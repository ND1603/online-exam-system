import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The RegisterScreen class allows new students to create an account.
 * It captures the username, password, and their specific department.
 */
public class RegisterScreen extends JFrame {
    public RegisterScreen() {
        // --- WINDOW CONFIGURATION ---
        setTitle("Create Account - Exam Portal");
        setSize(400, 600); // Increased height to fit the new Department dropdown
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // 1. Main Panel Setup
        // BoxLayout.Y_AXIS is used to stack the registration form elements vertically
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);

        // 2. Header
        JLabel lblTitle = new JLabel("Register");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(41, 128, 185));

        // 3. Input Fields for Credentials
        JTextField txtUser = new JTextField();
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JPasswordField txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // --- 4. DEPARTMENT SELECTION ---
        // JComboBox provides a controlled list of choices, preventing user typos
        String[] departments = {"Software Engineering", "Information System", "Computer Science", "Engineering"};
        JComboBox<String> comboDept = new JComboBox<>(departments);
        comboDept.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comboDept.setBackground(Color.WHITE);

        // 5. Register Button Styling
        JButton btnRegister = new JButton("Create Account");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnRegister.setBackground(new Color(39, 174, 96)); // Success Green color
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 6. Navigation Link (Back to Login)
        JLabel lblBack = new JLabel("Already have an account? Login here");
        lblBack.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblBack.setForeground(new Color(41, 128, 185));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listener to return to the LoginScreen
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginScreen().setVisible(true);
                dispose();
            }
        });

        // 7. Assemble the Layout (UI Order)
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        
        mainPanel.add(new JLabel("Choose Username:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtUser);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        mainPanel.add(new JLabel("Choose Password:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtPass);

        // Add Department selection area
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(new JLabel("Select Department:"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(comboDept);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(btnRegister);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(lblBack);

        add(mainPanel);

        // --- 8. REGISTRATION LOGIC ---
        btnRegister.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            String d = (String) comboDept.getSelectedItem(); // Retrieve the chosen department

            // Validation: Prevent empty submissions
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
            } else {
                // Pass username, password, and department to the DatabaseManager
                boolean success = DatabaseManager.registerStudent(u, p, d);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!");
                    new LoginScreen().setVisible(true); // Redirect to Login
                    dispose(); // Close registration screen
                } else {
                    // This triggers if the SQL INSERT fails (usually due to a UNIQUE constraint on username)
                    JOptionPane.showMessageDialog(this, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}