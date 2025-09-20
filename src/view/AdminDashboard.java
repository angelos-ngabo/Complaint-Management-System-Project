/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import dao.ComplaintDAO;
import dao.ReplyDAO;
import model.Complaint;
import model.Reply;
import model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import view.LoginForm;

/**
 *
 * @author ngabo
 */
public class AdminDashboard extends javax.swing.JFrame {
    private User admin; // The logged-in admin user
private ComplaintDAO complaintDAO;
private ReplyDAO replyDAO;

   public AdminDashboard(User admin) {
    this.admin = admin;
    this.complaintDAO = new ComplaintDAO();
    this.replyDAO = new ReplyDAO();
    initComponents();
    customizeUI();
    loadComplaints();
    updateUserInfo();
    
    // Add table selection listener
    complaintsTable.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && complaintsTable.getSelectedRow() != -1) {
            loadRepliesForSelectedComplaint();
        }
    });
}

private void loadComplaints() {
    try {
        List<Complaint> complaints = complaintDAO.getAllComplaints();
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "User ID", "Title", "Description", "Status", "Created At", "Updated At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Complaint c : complaints) {
            model.addRow(new Object[]{
                c.getComplaintId(),
                c.getUserId(),
                c.getTitle(),
                c.getDescription().length() > 50 ? 
                    c.getDescription().substring(0, 50) + "..." : c.getDescription(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
            });
        }
        
        complaintsTable.setModel(model);
        configureTableDisplay();
        
    } catch (Exception ex) {
        showError("Error loading complaints: " + ex.getMessage());
        ex.printStackTrace();
    }
}
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color1 = new Color(70, 130, 180);  
            Color color2 = new Color(135, 206, 250);  
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    

    /**
     * Creates new form AdminDashboard
     */

private void customizeUI() {
    // Set window title with admin username
    setTitle("CMS - Admin: " + admin.getUsername());
    
    // Customize table appearance
    complaintsTable.setRowHeight(25); // Changed from 100 to 25 for better row height
    complaintsTable.setShowGrid(false);
    complaintsTable.setIntercellSpacing(new Dimension(0, 0));
    complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Enable horizontal scrolling
    jScrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    // Customize table header
    JTableHeader header = complaintsTable.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(new Color(50, 50, 50));
    header.setForeground(Color.WHITE);
}

private void updateUserInfo() {
    jLabel3.setText("Logged in as: " + admin.getFullName());
}

private void loadRepliesForSelectedComplaint() {
    int selectedRow = complaintsTable.getSelectedRow();
    if (selectedRow != -1) {
        int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
        try {
            Complaint complaint = complaintDAO.getComplaintById(complaintId);
            
            StringBuilder sb = new StringBuilder();
            sb.append("Complaint #").append(complaintId).append("\n");
            sb.append("User ID: ").append(complaint.getUserId()).append("\n");
            sb.append("Status: ").append(complaint.getStatus()).append("\n\n");
            
            List<Reply> replies = complaint.getReplies();
            if (replies != null && !replies.isEmpty()) {
                for (Reply reply : replies) {
                    String sender = reply.isAdminReply() ? "[ADMIN]" : "[USER]";
                    sb.append(sender).append(" ").append(reply.getMessage()).append("\n");
                    sb.append("Posted at: ").append(reply.getCreatedAt()).append("\n\n");
                }
            } else {
                sb.append("No replies yet\n");
            }
            
            replyDisplay1.setText(sb.toString());
            replyDisplay1.setCaretPosition(0);
            
        } catch (SQLException ex) {
            showError("Error loading replies: " + ex.getMessage());
        }
    }
}

private void configureTableDisplay() {
    // Set preferred column widths
    TableColumnModel columnModel = complaintsTable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(50);    // ID
    columnModel.getColumn(1).setPreferredWidth(70);    // User ID
    columnModel.getColumn(2).setPreferredWidth(200);   // Title
    columnModel.getColumn(3).setPreferredWidth(350);   // Description
    columnModel.getColumn(4).setPreferredWidth(100);   // Status
    columnModel.getColumn(5).setPreferredWidth(150);   // Created At
    columnModel.getColumn(6).setPreferredWidth(150);   // Updated At
    
    // Change this to allow column resizing
    complaintsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    
    // Make the table fill the available width
    complaintsTable.setFillsViewportHeight(true);
    
    // Improve row appearance
    complaintsTable.setRowHeight(25);
    complaintsTable.setShowGrid(false);
    complaintsTable.setIntercellSpacing(new Dimension(0, 0));
    
    // Ensure horizontal scrolling is available if needed
    jScrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userPanel = new GradientPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        contentPanel1 = new javax.swing.JPanel();
        ReplyPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        complaintsTable = new javax.swing.JTable();
        replypane1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        replyDisplay1 = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        replyTextArea1 = new javax.swing.JTextArea();
        btnAddReply1 = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnDeleteComplaint = new javax.swing.JButton();
        btnUpdateStatus = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        statusCombo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CMS - Admin: [username]");

        userPanel.setBackground(new java.awt.Color(0, 153, 204));
        userPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userPanel.setPreferredSize(new java.awt.Dimension(850, 760));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("AdminDashboard");

        btnLogout.setBackground(new java.awt.Color(255, 102, 102));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        contentPanel1.setBackground(new java.awt.Color(255, 236, 233));
        contentPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel1.setMinimumSize(new java.awt.Dimension(1200, 400));
        contentPanel1.setPreferredSize(new java.awt.Dimension(1200, 400));

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.6);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(1200, 400));

        complaintsTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        complaintsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Title", "Description", "Status", "Created At", "Updated At"
            }
        ));
        complaintsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        complaintsTable.setMinimumSize(new java.awt.Dimension(1200, 400));
        complaintsTable.setPreferredSize(new java.awt.Dimension(1200, 400));
        complaintsTable.getTableHeader().setResizingAllowed(false);
        complaintsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(complaintsTable);

        jSplitPane2.setTopComponent(jScrollPane4);

        replypane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10), javax.swing.BorderFactory.createTitledBorder(null, "Conversation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 15)))); // NOI18N

        replyDisplay1.setColumns(20);
        replyDisplay1.setLineWrap(true);
        replyDisplay1.setRows(5);
        jScrollPane5.setViewportView(replyDisplay1);

        replyTextArea1.setColumns(20);
        replyTextArea1.setRows(5);
        jScrollPane6.setViewportView(replyTextArea1);

        btnAddReply1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddReply1.setText("Send Reply");
        btnAddReply1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddReply1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout replypane1Layout = new javax.swing.GroupLayout(replypane1);
        replypane1.setLayout(replypane1Layout);
        replypane1Layout.setHorizontalGroup(
            replypane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replypane1Layout.createSequentialGroup()
                .addGroup(replypane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(replypane1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5))
                    .addGroup(replypane1Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1069, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddReply1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        replypane1Layout.setVerticalGroup(
            replypane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replypane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(replypane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(btnAddReply1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jSplitPane2.setBottomComponent(replypane1);

        javax.swing.GroupLayout ReplyPanel1Layout = new javax.swing.GroupLayout(ReplyPanel1);
        ReplyPanel1.setLayout(ReplyPanel1Layout);
        ReplyPanel1Layout.setHorizontalGroup(
            ReplyPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReplyPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2))
        );
        ReplyPanel1Layout.setVerticalGroup(
            ReplyPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnDeleteComplaint.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteComplaint.setText("Delete Complaint");
        btnDeleteComplaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteComplaintActionPerformed(evt);
            }
        });

        btnUpdateStatus.setBackground(new java.awt.Color(255, 102, 102));
        btnUpdateStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdateStatus.setText("Update Status");
        btnUpdateStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusActionPerformed(evt);
            }
        });

        jLabel2.setText("Update Status");

        statusCombo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        statusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pending", "in_progress", "resolved" }));

        javax.swing.GroupLayout contentPanel1Layout = new javax.swing.GroupLayout(contentPanel1);
        contentPanel1.setLayout(contentPanel1Layout);
        contentPanel1Layout.setHorizontalGroup(
            contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(statusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(btnUpdateStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteComplaint, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(401, 401, 401)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(99, 99, 99))
            .addGroup(contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contentPanel1Layout.createSequentialGroup()
                    .addComponent(ReplyPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 82, Short.MAX_VALUE)))
        );
        contentPanel1Layout.setVerticalGroup(
            contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanel1Layout.createSequentialGroup()
                .addContainerGap(614, Short.MAX_VALUE)
                .addGroup(contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDeleteComplaint, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdateStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(statusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(contentPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contentPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ReplyPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(69, Short.MAX_VALUE)))
        );

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Logged in as: [Full Name]");

        javax.swing.GroupLayout userPanelLayout = new javax.swing.GroupLayout(userPanel);
        userPanel.setLayout(userPanelLayout);
        userPanelLayout.setHorizontalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnLogout)
                .addGap(116, 116, 116))
            .addGroup(userPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contentPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        userPanelLayout.setVerticalGroup(
            userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPanelLayout.createSequentialGroup()
                .addGroup(userPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogout))
                .addGap(15, 15, 15)
                .addComponent(contentPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(userPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 795, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteComplaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteComplaintActionPerformed
 int selectedRow = complaintsTable.getSelectedRow();
    if (selectedRow == -1) {
        showError("Please select a complaint first");
        return;
    }
    
    int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
    
    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete this complaint and all its replies?",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // First delete all replies for this complaint
            replyDAO.deleteRepliesByComplaint(complaintId);
            
            // Then delete the complaint
            if (complaintDAO.deleteComplaint(complaintId)) {
                loadComplaints();
                replyDisplay1.setText("");
                showMessage("Complaint and all replies deleted successfully!");
            } else {
                showError("Failed to delete complaint");
            }
        } catch (Exception ex) {
            showError("Error deleting complaint: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    }//GEN-LAST:event_btnDeleteComplaintActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadComplaints();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnAddReply1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddReply1ActionPerformed
        // TODO add your handling code here:
     int selectedRow = complaintsTable.getSelectedRow();
    if (selectedRow == -1) {
        showError("Please select a complaint first");
        return;
    }

    String message = replyTextArea1.getText().trim();
    if (message.isEmpty()) {
        showError("Please enter a reply message");
        return;
    }

    int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);

    try {
        Reply reply = new Reply(complaintId, admin.getUserId(), message, true);

        if (replyDAO.addReply(reply)) {
            replyTextArea1.setText("");
            loadRepliesForSelectedComplaint();
            showMessage("Reply added successfully!");
        } else {
            showError("Failed to add reply");
        }
    } catch (Exception ex) {
        showError("Error adding reply: " + ex.getMessage());
    }
        
    }//GEN-LAST:event_btnAddReply1ActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        new LoginForm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusActionPerformed
        int selectedRow = complaintsTable.getSelectedRow();
    if (selectedRow == -1) {
        showError("Please select a complaint first");
        return;
    }
    
    int complaintId = (int) complaintsTable.getValueAt(selectedRow, 0);
    String newStatus = (String) statusCombo.getSelectedItem();
    
    try {
        if (complaintDAO.updateComplaintStatus(complaintId, newStatus)) {
            // Refresh the complaints list and replies
            loadComplaints();
            loadRepliesForSelectedComplaint();
            showMessage("Status updated successfully to: " + newStatus);
        } else {
            showError("Failed to update status");
        }
    } catch (Exception ex) {
        showError("Error updating status: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnUpdateStatusActionPerformed
   class StatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        String status = value.toString().toLowerCase();
        switch (status) {
            case "pending":
                c.setBackground(new Color(255, 243, 224, 200));
                c.setForeground(new Color(255, 152, 0));
                break;
            case "in_progress":
                c.setBackground(new Color(224, 242, 241, 200));
                c.setForeground(new Color(0, 150, 136));
                break;
            case "resolved":
                c.setBackground(new Color(232, 245, 233, 200));
                c.setForeground(new Color(56, 142, 60));
                break;
            default:
                c.setBackground(table.getBackground());
                c.setForeground(table.getForeground());
        }
        
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        }
        
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        return c;
    }
}
   private void showMessage(String message) {
    JOptionPane.showMessageDialog(this, 
        message, 
        "Success", 
        JOptionPane.INFORMATION_MESSAGE);
}

private void showError(String message) {
    JOptionPane.showMessageDialog(this, 
        "Error: " + message, 
        "Error", 
        JOptionPane.ERROR_MESSAGE);
}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ReplyPanel1;
    private javax.swing.JButton btnAddReply1;
    private javax.swing.JButton btnDeleteComplaint;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUpdateStatus;
    private javax.swing.JTable complaintsTable;
    private javax.swing.JPanel contentPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextArea replyDisplay1;
    private javax.swing.JTextArea replyTextArea1;
    private javax.swing.JPanel replypane1;
    private javax.swing.JComboBox<String> statusCombo;
    private javax.swing.JPanel userPanel;
    // End of variables declaration//GEN-END:variables
}
