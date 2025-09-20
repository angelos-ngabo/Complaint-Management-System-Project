package view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            UIManager.put("OptionPane.background", new Color(240, 240, 240));
            UIManager.put("Panel.background", new Color(240, 240, 240));
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("MySQL driver loaded successfully");
                
                try {
                    if (dao.DBConnection.getConnection() != null) {
                        System.out.println("Database connection successful");
                    }
                } catch (SQLException ex) {
                    showErrorDialog(null, "Failed to connect to database: " + ex.getMessage());
                    return;
                }
            } catch (ClassNotFoundException e) {
                showErrorDialog(null, "MySQL Driver not found. Please ensure the driver is in your classpath.");
                return;
            }
            
            showSplashScreen();
            
            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
            
        } catch (Exception e) {
            showErrorDialog(null, "Application initialization failed: " + e.getMessage());
        }
    }

    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        JPanel splashPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(101, 116, 255), 
                    getWidth(), getHeight(), new Color(111, 186, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        JLabel title = new JLabel("Complaint Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        
        splashPanel.add(title, BorderLayout.CENTER);
        splashPanel.add(progressBar, BorderLayout.SOUTH);
        
        splash.setContentPane(splashPanel);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            splash.dispose();
        }).start();
    }

    private static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            "<html><div style='text-align: center;'>" + message + "</div></html>", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}