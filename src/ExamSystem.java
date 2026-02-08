import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * The ExamSystem class handles the exam logic, including navigation and scoring.
 */
public class ExamSystem extends JFrame {
    private JLabel lblQuestion, lblTimer, lblCount;
    private JRadioButton[] options = new JRadioButton[4]; 
    private ButtonGroup group; 
    private JButton btnNext, btnBack; // Added btnBack
    private List<Question> questionList; 
    private String[] studentAnswers; // Array to "remember" student choices
    
    private int currentIdx = 0; 
    private int score = 0; 
    private int timeLeft = 3600; 

    public ExamSystem(String studentName, String department) {
        // --- WINDOW CONFIGURATION ---
        setTitle("CBE Portal - Examination");
        setSize(750, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250)); 

        // --- 1. TOP PANEL ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(41, 128, 185)); 
        topPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblUser = new JLabel("Student: " + studentName + " (" + department + ")");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblTimer = new JLabel("Time Remaining: 60:00");
        lblTimer.setForeground(Color.WHITE);
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 16)); 

        topPanel.add(lblUser, BorderLayout.WEST);
        topPanel.add(lblTimer, BorderLayout.EAST);

        // --- 2. CENTER PANEL ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(30, 50, 30, 50),
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));

        lblCount = new JLabel("Loading...");
        lblCount.setForeground(Color.GRAY);
        
        lblQuestion = new JLabel("<html><body style='width: 450px'>Loading question...</body></html>");
        lblQuestion.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblQuestion.setBorder(new EmptyBorder(10, 0, 20, 0));

        cardPanel.add(lblCount);
        cardPanel.add(lblQuestion);

        group = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setBackground(Color.WHITE);
            options[i].setFont(new Font("SansSerif", Font.PLAIN, 15));
            options[i].setFocusPainted(false);
            group.add(options[i]);
            cardPanel.add(options[i]);
        }

        // --- 3. BOTTOM PANEL (NAVIGATION) ---
        // Using FlowLayout to align both buttons to the right
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(new Color(245, 247, 250));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 30, 50));

        btnBack = new JButton("← Previous");
        btnBack.setPreferredSize(new Dimension(150, 45));
        btnBack.setBackground(new Color(149, 165, 166)); // Neutral Gray
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setVisible(false); // Hidden on the first question

        btnNext = new JButton("Next Question →");
        btnNext.setPreferredSize(new Dimension(180, 45));
        btnNext.setBackground(new Color(39, 174, 96)); 
        btnNext.setForeground(Color.WHITE);
        btnNext.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottomPanel.add(btnBack);
        bottomPanel.add(btnNext);

        add(topPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. DATA & LOGIC ---
        questionList = DatabaseManager.getQuestionsByDepartment(department);

        if (questionList == null || questionList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for department: " + department);
            this.dispose(); 
            return;
        }

        // Initialize the array to store student answers
        studentAnswers = new String[questionList.size()];
        
        displayQuestion(); 

        Timer timer = new Timer(1000, e -> {
            timeLeft--;
            int mins = timeLeft / 60;
            int secs = timeLeft % 60;
            lblTimer.setText(String.format("Time Remaining: %02d:%02d", mins, secs));
            if (timeLeft <= 300) lblTimer.setForeground(new Color(231, 76, 60)); 
            if (timeLeft <= 0) {
                ((Timer)e.getSource()).stop();
                calculateFinalScore();
                finishExam(studentName);
            }
        });
        timer.start();

        // --- ACTION LISTENERS ---

        btnNext.addActionListener(e -> {
            saveCurrentAnswer(); // Save choice before moving
            if (currentIdx < questionList.size() - 1) {
                currentIdx++;
                displayQuestion();
                btnBack.setVisible(true); // Always show back button after question 1
                
                if(currentIdx == questionList.size() - 1) {
                    btnNext.setText("Finish Exam");
                    btnNext.setBackground(new Color(231, 76, 60)); 
                }
            } else {
                timer.stop();
                calculateFinalScore();
                finishExam(studentName);
            }
        });

        btnBack.addActionListener(e -> {
            saveCurrentAnswer(); // Save choice before moving back
            if (currentIdx > 0) {
                currentIdx--;
                displayQuestion();
                
                // Reset Next button appearance if moving back from the last question
                btnNext.setText("Next Question →");
                btnNext.setBackground(new Color(39, 174, 96));
                
                // Hide back button if we are at the beginning
                if (currentIdx == 0) btnBack.setVisible(false);
            }
        });
    }

    /** Stores the student's current radio button selection into the answer array */
    private void saveCurrentAnswer() {
        String[] keys = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) {
                studentAnswers[currentIdx] = keys[i];
                return;
            }
        }
    }

    /** Updates the UI and restores any previously saved answer for this question */
    private void displayQuestion() {
        Question q = questionList.get(currentIdx);
        lblCount.setText("Question " + (currentIdx + 1) + " of " + questionList.size());
        lblQuestion.setText("<html><body style='width: 500px'>" + q.text + "</body></html>");
        options[0].setText(q.optionA);
        options[1].setText(q.optionB);
        options[2].setText(q.optionC);
        options[3].setText(q.optionD);
        
        group.clearSelection(); 
        
        // Restore saved answer if the student had already picked one
        if (studentAnswers[currentIdx] != null) {
            String saved = studentAnswers[currentIdx];
            if (saved.equals("A")) options[0].setSelected(true);
            else if (saved.equals("B")) options[1].setSelected(true);
            else if (saved.equals("C")) options[2].setSelected(true);
            else if (saved.equals("D")) options[3].setSelected(true);
        }
    }

    /** Loops through the answer array and compares against the correct database answers */
    private void calculateFinalScore() {
        score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            if (studentAnswers[i] != null && studentAnswers[i].equals(questionList.get(i).correct)) {
                score++;
            }
        }
    }

    private void finishExam(String name) {
        DatabaseManager.saveScore(name, score, questionList.size());
        JPanel resPanel = new JPanel(new GridLayout(2, 1));
        JLabel l1 = new JLabel("Exam Complete, " + name + "!", JLabel.CENTER);
        JLabel l2 = new JLabel("Score: " + score + " / " + questionList.size(), JLabel.CENTER);
        l2.setFont(new Font("SansSerif", Font.BOLD, 22));
        l2.setForeground(new Color(41, 128, 185));
        resPanel.add(l1); resPanel.add(l2);
        JOptionPane.showMessageDialog(this, resPanel, "Results", JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }
}