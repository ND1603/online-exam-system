import javax.swing.SwingUtilities;

public class OnlineExamPortal {
    public static void main(String[] args) {
        // This starts the whole application
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
