package view;

import dao.ComplaintDAO;
import java.sql.SQLException;
import model.Complaint;
import model.User;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Date;

public class ComplaintForm extends javax.swing.JFrame {
    private final User currentUser;
    
    // Constructor that accepts User object
    public ComplaintForm(User user) {
        this.currentUser = user;
        initComponents();
        setupForm();
    }

    private void setupForm() {
        System.out.println("Submit button enabled: " + btnSubmit.isEnabled());
        btnSubmit.setEnabled(true);
        txtTitle.requestFocusInWindow();
        
        // Enable/disable submit button based on input
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) { validateFields(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateFields(); }
            @Override
            public void insertUpdate(DocumentEvent e) { validateFields(); }
        };
        
        txtTitle.getDocument().addDocumentListener(docListener);
        TxtDescription.getDocument().addDocumentListener(docListener);
        
        // Initialize button states
        validateFields();
    }

private void submitComplaint() {
    try {
        // Debug: Verify method is being called
        System.out.println("submitComplaint() method called");
        
        // Get and trim input values
        String title = txtTitle.getText().trim();
        String description = TxtDescription.getText().trim();
        
        // Validate inputs
        if (!validateInputs(title, description)) {
            return;
        }
        
        // Create complaint object
        Complaint complaint = new Complaint();
        complaint.setUserId(currentUser.getId());
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setStatus("Pending");
        // Date is automatically set in Complaint constructor
        
        // Submit to database
        ComplaintDAO complaintDAO = new ComplaintDAO();
        boolean result = complaintDAO.addComplaint(complaint);
        
        if (result) {
            showSuccess("Complaint submitted successfully!");
            clearForm();
        } else {
            showError("Failed to submit complaint. Please try again.");
        }
    } catch (SQLException e) {
        String errorMsg = "Database error: " + e.getMessage();
        if (e.getMessage().contains("connection")) {
            errorMsg += "\nPlease check your database connection.";
        }
        showError(errorMsg);
        e.printStackTrace();
    } catch (Exception e) {
        showError("Unexpected error: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void cancelForm() {
        if (!txtTitle.getText().isEmpty() || !TxtDescription.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to discard this complaint?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
            }
        } else {
            this.dispose();
        }
    }
    
    private void resetForm() {
        txtTitle.setText("");
        TxtDescription.setText("");
        txtTitle.requestFocusInWindow();
    }
    
    private boolean validateInputs(String title, String description) {
        if (title.isEmpty()) {
            showError("Please enter a title for your complaint");
            txtTitle.requestFocus();
            return false;
        }
        
        if (description.isEmpty()) {
            showError("Please describe your complaint in detail");
            TxtDescription.requestFocus();
            return false;
        }
        
        if (title.length() < 5) {
            showError("Title must be at least 5 characters long");
            txtTitle.requestFocus();
            return false;
        }
        
        if (description.length() < 20) {
            showError("Description must be at least 20 characters long");
            TxtDescription.requestFocus();
            return false;
        }
        
        if (title.length() > 100) {
            showError("Title cannot exceed 100 characters");
            txtTitle.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void validateFields() {
        boolean isValid = !txtTitle.getText().trim().isEmpty() 
                       && !TxtDescription.getText().trim().isEmpty()
                       && txtTitle.getText().trim().length() >= 5
                       && TxtDescription.getText().trim().length() >= 20;
        btnSubmit.setEnabled(isValid);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void clearForm() {
        txtTitle.setText("");
        TxtDescription.setText("");
        txtTitle.requestFocusInWindow();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnSubmit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TxtDescription = new javax.swing.JTextArea();
        btnCancel1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(700, 600));

        jPanel1.setBackground(new java.awt.Color(0, 153, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Title:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Description:");

        btnSubmit.setBackground(new java.awt.Color(255, 107, 107));
        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Submit New Complaint");

        txtTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTitleActionPerformed(evt);
            }
        });

        TxtDescription.setColumns(20);
        TxtDescription.setRows(5);
        jScrollPane1.setViewportView(TxtDescription);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 319, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        btnCancel1.setBackground(new java.awt.Color(255, 107, 107));
        btnCancel1.setText("Reset");
        btnCancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancel1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(228, 228, 228)
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(221, 221, 221)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtTitle))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(146, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(102, 102, 102))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        submitComplaint();
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTitleActionPerformed
        // TODO add your handling code here:
        TxtDescription.requestFocusInWindow();
    }//GEN-LAST:event_txtTitleActionPerformed

    private void btnCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancel1ActionPerformed
        // TODO add your handling code here:
        resetForm();
    }//GEN-LAST:event_btnCancel1ActionPerformed
@Override
    public void dispose() {
        if (!txtTitle.getText().isEmpty() || !TxtDescription.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "You have unsaved changes. Close anyway?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        super.dispose();
    }
    /**
     * @param args the command line arguments
     */
 


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea TxtDescription;
    private javax.swing.JButton btnCancel1;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(() -> {
        try {
            User testUser = new User();
            // First set ID before trying to use it
            
            new ComplaintForm(testUser).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize test user: " + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
        }
    });
}}