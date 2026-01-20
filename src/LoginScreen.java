
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class LoginScreen extends JPanel implements ActionListener {

    private JFrame mainFrame;
    private JRadioButton adminRadio, userRadio;
    private JButton loginButton, registerButton;
    private JLabel messageLabel;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[^@]+@[^@]+$"
    );

    public LoginScreen(JFrame frame) {
        this.mainFrame = frame;
        
        setLayout(new GridBagLayout()); 
       
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("BookMyCar System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36)); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        add(title, gbc);

        messageLabel = new JLabel("Select Role and click Login or Register.", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18)); 
        messageLabel.setForeground(new Color(0, 100, 200)); 
        gbc.gridy = 1;
        add(messageLabel, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10)); 
        adminRadio = new JRadioButton("Admin");
        userRadio = new JRadioButton("User", true); 
        
        Font radioFont = new Font("Arial", Font.PLAIN, 18);
        adminRadio.setFont(radioFont);
        userRadio.setFont(radioFont);
        
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(userRadio);
        rolePanel.add(new JLabel("Role:"));
        rolePanel.add(adminRadio);
        rolePanel.add(userRadio);
        
        gbc.gridy = 2;
        add(rolePanel, gbc);

        
        gbc.gridwidth = 1; 
        gbc.weightx = 0.5; 
        
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 50)); 
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.addActionListener(this);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 50)); 
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.addActionListener(this);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(registerButton, gbc);
        
        mainFrame.pack();

        mainFrame.setMinimumSize(new Dimension(500, 450));
        mainFrame.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            showLoginDialog();
        } else if (e.getSource() == registerButton) {

            if (userRadio.isSelected()) {
                showRegistrationDialog();
            } else {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Only User accounts can be registered.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLoginDialog() {
        boolean isAdmin = adminRadio.isSelected();
        String role = isAdmin ? "Admin" : "User";

        // Panel for input fields
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10)); // Increased spacing
        JTextField userField = new JTextField(20); // Longer field
        JPasswordField passField = new JPasswordField(20); // Longer field

        // Use a wrapper panel for better dialog appearance
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JLabel("Enter " + role + " Credentials", SwingConstants.CENTER), BorderLayout.NORTH);
        
        panel.add(new JLabel("Username (Name):"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        
        wrapper.add(panel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(mainFrame, wrapper, 
                role + " Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            handleLogin(username, password, isAdmin);
        }
    }
    
    private void showRegistrationDialog() {
        // Panel for input fields (Requires email)
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        JTextField userField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);

        // Use a wrapper panel for better dialog appearance
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JLabel("Register New User Account", SwingConstants.CENTER), BorderLayout.NORTH);

        panel.add(new JLabel("Username (Name):"));
        panel.add(userField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        
        wrapper.add(panel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(mainFrame, wrapper, 
                "User Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = userField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            handleRegistration(username, email, password);
        }
    }

    private void handleLogin(String username, String password, boolean isAdmin) {
        if (isAdmin) {
            if (BookMyCarAppGUI.ADMIN.login(username, password)) {
                JOptionPane.showMessageDialog(mainFrame, "Login Successful! Redirecting to Admin menu.", "Success", JOptionPane.INFORMATION_MESSAGE);
                BookMyCarAppGUI.showAdminMenu();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "❌ Admin credentials incorrect!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            User user = BookMyCarAppGUI.findUser(username);
            if (user != null && user.login(password)) {
                JOptionPane.showMessageDialog(mainFrame, "Login Successful! Redirecting to User menu.", "Success", JOptionPane.INFORMATION_MESSAGE);
                BookMyCarAppGUI.showUserMenu(user);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "❌ Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
//    private void handleRegistration(String username, String email, String password) {
//        // 1. Check if user already exists
//        if (BookMyCarAppGUI.findUser(username) != null) {
//            JOptionPane.showMessageDialog(mainFrame, "❌ User '" + username + "' already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // 2. Email Format Validation
//        if (!isValidEmail(email)) {
//            JOptionPane.showMessageDialog(mainFrame, "❌ Invalid email format (must contain @).", "Registration Failed", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        
//        // 3. Successful Registration
//        BookMyCarAppGUI.registerUser(username, email, password);
//        
//        messageLabel.setForeground(Color.GREEN);
//        messageLabel.setText("✅ Registration successful! Please log in.");
//        
//        JOptionPane.showMessageDialog(mainFrame, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
//    }
    private void handleRegistration(String username, String email, String password) {
        // 1. Basic Field Validation (Updated check)
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Name, Email, and Password are required for registration.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Check if user already exists
        if (BookMyCarAppGUI.findUser(username) != null) {
            JOptionPane.showMessageDialog(mainFrame, "❌ User '" + username + "' already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Email Format Validation
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(mainFrame, "❌ Invalid email format (must contain @).", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 4. Successful Registration
        BookMyCarAppGUI.registerUser(username, email, password);
        
        messageLabel.setForeground(Color.GREEN);
        messageLabel.setText("✅ Registration successful! Please log in.");
        
        JOptionPane.showMessageDialog(mainFrame, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches(); 
    }
}