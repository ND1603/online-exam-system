import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        // Basic Frame Setup
        setTitle("Exam Portal");
        setSize(400, 550); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Main Panel Construction
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);

        // 2. Header Section (Matching the "Register" look)
        JLabel lblTitle = new JLabel("Exam Portal");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32)); // Larger like the Register screen
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(41, 128, 185)); // Using your blue color

        // 3. Input Fields Setup
        JTextField txtUser = new JTextField();
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JPasswordField txtPass = new JPasswordField();
       txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // 4. Styled Login Button (Green like the Register screen)
        JButton btnLogin = new JButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        // Changing to green to match your "Create Account" button
        btnLogin.setBackground(new Color(39, 174, 96)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 5. Navigation Link
        JLabel lblRegister = new JLabel("Don't have an account? Register here");
        lblRegister.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRegister.setForeground(new Color(41, 128, 185));
        lblRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterScreen().setVisible(true);
                dispose();
            }
        });

        // 6. Build Layout with Bold Labels 
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Username Label (Bolded)
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtUser);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password Label (Bolded)
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtPass);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(btnLogin);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(lblRegister);

        add(mainPanel);

        // --- AUTHENTICATION LOGIC ---
        btnLogin.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            
            if (u.equals("admin") && p.equals("admin123")) {
                new AdminPanel().setVisible(true);
                this.dispose();
            } else if (DatabaseManager.loginStudent(u, p)) {
                new StudentDashboard(u).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}