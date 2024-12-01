package traveldart;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.border.Border;

public class Login extends JFrame {

    // Variables declaration
    private JLabel accountVariable;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JButton loginButton;
    private JPasswordField password;
    private JTextField username;

    public Login() {
        initComponents();
    }

    private void initComponents() {
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        password = new JPasswordField();
        username = new JTextField();
        accountVariable = new JLabel();
        loginButton = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set jPanel1 layout
        jPanel1.setBackground(new Color(0, 102, 102));
        jPanel1.setLayout(new BorderLayout());

        // Configure title label
        jLabel1.setFont(new Font("Sitka Text", Font.BOLD, 60));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Login");
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);

        // Add title label to the top (NORTH) of jPanel1
        jPanel1.add(jLabel1, BorderLayout.NORTH);

        // Create a center panel to hold form fields
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(0, 102, 102));
        centerPanel.setLayout(new GridLayout(4, 2, 10, 10)); // 4 rows, 2 columns

        // Configure username label and add to center panel
        jLabel3.setFont(new Font("Yu Gothic", Font.PLAIN, 18));
        jLabel3.setForeground(Color.WHITE);
        jLabel3.setText("Username");
        centerPanel.add(jLabel3);

        // Add username field to center panel
        centerPanel.add(username);

        // Configure password label and add to center panel
        jLabel4.setFont(new Font("Yu Gothic", Font.PLAIN, 18));
        jLabel4.setForeground(Color.WHITE);
        jLabel4.setText("Password");
        centerPanel.add(jLabel4);

        // Add password field to center panel
        centerPanel.add(password);

        // Add center panel to jPanel1 in the CENTER position
        jPanel1.add(centerPanel, BorderLayout.CENTER);

        // Configure Sign Up link
        accountVariable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountVariable.setText(">>> Don't have an account? Sign Up");
        accountVariable.setForeground(Color.WHITE);
        accountVariable.setHorizontalAlignment(SwingConstants.CENTER);
        accountVariable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                accountVariableMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                accountVariableMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                accountVariableMouseExited(evt);
            }
        });

        // Add Sign Up link to jPanel1 at the bottom (SOUTH)
        jPanel1.add(accountVariable, BorderLayout.SOUTH);

        // Configure Login button
        loginButton.setBackground(new Color(153, 153, 0));
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        loginButton.setText("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        // Create a button panel and add the Login button to it
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 102, 102));
        buttonPanel.add(loginButton);

        // Add button panel to jPanel1 at the bottom of centerPanel
        jPanel1.add(buttonPanel, BorderLayout.PAGE_END);

        // Set the layout for the JFrame and add jPanel1
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jPanel1, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void accountVariableMouseClicked(java.awt.event.MouseEvent evt) {
        SignUp refForm = new SignUp();
        refForm.setVisible(true);
        refForm.pack();
        refForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.dispose();
    }

    private void accountVariableMouseEntered(java.awt.event.MouseEvent evt) {
        Border label = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.red);
        accountVariable.setBorder(label);
    }

    private void accountVariableMouseExited(java.awt.event.MouseEvent evt) {
        Border label2 = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray);
        accountVariable.setBorder(label2);
    }

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
    Connection connection = null;
    PreparedStatement pst = null;
    ResultSet resultSet = null;

    String query = "SELECT * FROM users WHERE username = ? AND password = ?";
    try {
        // Establish connection
        connection = DatabaseConnection.connect();

        // Prepare the statement with placeholders
        pst = connection.prepareStatement(query);
        pst.setString(1, username.getText());
        pst.setString(2, password.getText());

        // Execute the query
        resultSet = pst.executeQuery();

        // Check if a user exists with the given credentials
        if (resultSet.next()) {
            // User found, login successful
            javax.swing.JOptionPane.showMessageDialog(null, "Login successful!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            HomePage hp = new HomePage();
            hp.setVisible(true);
            this.setVisible(false);
            
        } else {
            // User not found, show error dialog
            javax.swing.JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.", "Login Failed", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        // Close resources after successful query execution
        resultSet.close();
        pst.close();
        connection.close();

    } catch (SQLException e) {
        // Show error dialog in case of database connection issues
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "An error occurred while connecting to the database.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);

       
    }
}
}
