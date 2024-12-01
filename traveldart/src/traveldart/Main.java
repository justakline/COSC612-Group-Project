package traveldart;

import javax.swing.*;

public class Main extends javax.swing.JFrame {

    public Main() {
        initComponents();
    }

    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(930, 660));

        // Set jPanel1 with BorderLayout
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());

        // Configure jButton1 (Sign In Button)
        jButton1.setBackground(new java.awt.Color(0, 204, 204));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton1.setText("Sign In");
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt));

        // Add Sign In button to the bottom (SOUTH) of jPanel1
        jPanel1.add(jButton1, java.awt.BorderLayout.SOUTH);

        // Configure jPanel3 with BorderLayout
        jPanel3.setBackground(new java.awt.Color(0, 153, 153));
        jPanel3.setLayout(new java.awt.BorderLayout());

        // Configure jLabel5 (Travel Dart Title)
        jLabel5.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 1, 55));
        jLabel5.setForeground(new java.awt.Color(153, 0, 102));
        jLabel5.setText("TRAVEL DART");

        // Add title to the top (NORTH) of jPanel3
        jPanel3.add(jLabel5, java.awt.BorderLayout.NORTH);

        // Add the images (using CENTER)
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/target2.gif")));
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        // Add jPanel3 to the top of jPanel1
        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        // Use an additional JPanel to hold other images in the center
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new java.awt.Color(255, 255, 255));
        centerPanel.setLayout(new java.awt.GridLayout(1, 3)); // Arrange images horizontally

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/knife and fork.gif")));
        centerPanel.add(jLabel2);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/location pin.gif")));
        centerPanel.add(jLabel3);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/concert.gif")));
        centerPanel.add(jLabel6);

        // Add the center panel with images to the CENTER of jPanel1
        jPanel1.add(centerPanel, java.awt.BorderLayout.CENTER);

        // Add jPanel1 to the JFrame
        getContentPane().add(jPanel1);

        pack();
        setLocationRelativeTo(null);
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        SignUp lp = new SignUp();
        this.dispose();
        lp.setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration
}
