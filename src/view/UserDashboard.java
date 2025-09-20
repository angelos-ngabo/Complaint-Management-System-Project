/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import dao.ComplaintDAO;
import dao.ReplyDAO;
import model.Complaint;
import model.Reply;
import model.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author ngabo
 */
public class UserDashboard extends javax.swing.JFrame {
    private User user; // The logged-in user
private ComplaintDAO complaintDAO = new ComplaintDAO();
private ReplyDAO replyDAO = new ReplyDAO();
    
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
     * Creates new form UserDashboard
     */
public UserDashboard(User user) {
    this.user = user;
    initComponents();
    customizeUI();
    loadComplaints();
    
    // Set welcome message
    jLabel1.setText("Welcome, " + user.getFullName() + "!");
    setTitle("CMS - User: " + user.getUsername());
    
    // Center the window
    setLocationRelativeTo(null);
}
   
private void customizeUI() {
    
    replyDisplay.setEditable(false);
    replyDisplay.setLineWrap(true);
    replyDisplay.setWrapStyleWord(true);
    replyDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
    replyDisplay.setMargin(new Insets(10, 10, 10, 10));
    replyDisplay.setBackground(new Color(255, 255, 255, 220));
    
    // Configure reply text area
    replyTextArea.setLineWrap(true);
    replyTextArea.setWrapStyleWord(true);
    replyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    replyTextArea.setMargin(new Insets(5, 5, 5, 5));
    
    // Add selection listener to table
    TableContents.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            showSelectedComplaintDetails();
        }
    });
    // Table configuration
    TableContents.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableContents.setRowHeight(25);
    TableContents.setFillsViewportHeight(true);
    TableContents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Scroll pane configuration
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane1.setViewportView(TableContents);
    
    // Table header styling
    JTableHeader header = TableContents.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(new Color(50, 50, 50));
    header.setForeground(Color.WHITE);
    header.setReorderingAllowed(false);
}

private void loadComplaints() {
    try {
        List<Complaint> complaints = complaintDAO.getComplaintsByUserId(user.getUserId());
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Description", "Status", "Created At", "Updated At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Complaint c : complaints) {
            model.addRow(new Object[]{
                c.getComplaintId(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
            });
        }
        
        TableContents.setModel(model);
        adjustColumnWidths();
        TableContents.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        
        // Select first row if available
        if (TableContents.getRowCount() > 0) {
            TableContents.setRowSelectionInterval(0, 0);
            showSelectedComplaintDetails();
        }
        
    } catch (SQLException ex) {
        showError("Error loading complaints: " + ex.getMessage());
    }
}

private void adjustColumnWidths() {
    TableColumnModel columnModel = TableContents.getColumnModel();
    
    // Set minimum preferred widths
    columnModel.getColumn(0).setPreferredWidth(50);   // ID
    columnModel.getColumn(1).setPreferredWidth(200);  // Title
    columnModel.getColumn(2).setPreferredWidth(350);  // Description
    columnModel.getColumn(3).setPreferredWidth(100);  // Status
    columnModel.getColumn(4).setPreferredWidth(150);  // Created At
    columnModel.getColumn(5).setPreferredWidth(150);  // Updated At
    
    // Auto-size columns based on content
    for (int column = 0; column < TableContents.getColumnCount(); column++) {
        TableColumn col = columnModel.getColumn(column);
        int width = 0;
        
        // Get header width
        TableCellRenderer headerRenderer = col.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = TableContents.getTableHeader().getDefaultRenderer();
        }
        Component headerComp = headerRenderer.getTableCellRendererComponent(
            TableContents, col.getHeaderValue(), false, false, 0, column);
        width = headerComp.getPreferredSize().width;
        
        // Get maximum cell width
        for (int row = 0; row < TableContents.getRowCount(); row++) {
            TableCellRenderer renderer = TableContents.getCellRenderer(row, column);
            Component comp = TableContents.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width, width);
        }
        
        // Add padding
        width += 10;
        col.setPreferredWidth(width);
    }
}


private void packTableColumns() {
    for (int column = 0; column < TableContents.getColumnCount(); column++) {
        TableColumn tableColumn = TableContents.getColumnModel().getColumn(column);
        int preferredWidth = tableColumn.getMinWidth();
        int maxWidth = tableColumn.getMaxWidth();
        
        for (int row = 0; row < TableContents.getRowCount(); row++) {
            TableCellRenderer cellRenderer = TableContents.getCellRenderer(row, column);
            Component c = TableContents.prepareRenderer(cellRenderer, row, column);
            int width = c.getPreferredSize().width + TableContents.getIntercellSpacing().width;
            preferredWidth = Math.max(preferredWidth, width);
            
            // Ensure we don't exceed max width
            if (preferredWidth >= maxWidth) {
                preferredWidth = maxWidth;
                break;
            }
        }
        
        tableColumn.setPreferredWidth(preferredWidth);
    }
}
private void showSelectedComplaintDetails() {
    int selectedRow = TableContents.getSelectedRow();
    if (selectedRow != -1) {
        int complaintId = (int) TableContents.getValueAt(selectedRow, 0);
        try {
            // Get the complaint
            Complaint complaint = complaintDAO.getComplaintById(complaintId);
            
            if (complaint != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("=== Complaint Details ===\n");
                sb.append("ID: ").append(complaint.getComplaintId()).append("\n");
                sb.append("Title: ").append(complaint.getTitle()).append("\n");
                sb.append("Status: ").append(complaint.getStatus()).append("\n");
                sb.append("Created: ").append(complaint.getCreatedAt()).append("\n");
                sb.append("\n=== Replies ===\n");
                
                // Get all replies for this complaint
                List<Reply> replies = replyDAO.getRepliesByComplaintId(complaintId);
                
                if (replies != null && !replies.isEmpty()) {
                    for (Reply reply : replies) {
                        String sender = reply.isAdminReply() ? "[ADMIN]" : "[YOU]";
                        sb.append("\n").append(sender).append(" at ")
                          .append(reply.getCreatedAt()).append(":\n");
                        sb.append(reply.getMessage()).append("\n");
                    }
                } else {
                    sb.append("\nNo replies yet\n");
                }
                
                replyDisplay.setText(sb.toString());
                replyDisplay.setCaretPosition(0);
            }
        } catch (SQLException ex) {
            showError("Error loading complaint details: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainPanel = new GradientPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        ReplyPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableContents = new javax.swing.JTable();
        replypane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        replyDisplay = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        replyTextArea = new javax.swing.JTextArea();
        btnAddReply = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnNewComplaint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CMS - User: ");
        setFocusTraversalPolicyProvider(true);
        setPreferredSize(new java.awt.Dimension(1414, 862));
        setResizable(false);
        setSize(new java.awt.Dimension(1414, 862));

        MainPanel.setBackground(new java.awt.Color(0, 153, 204));
        MainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        MainPanel.setMinimumSize(new java.awt.Dimension(1200, 400));
        MainPanel.setPreferredSize(new java.awt.Dimension(850, 760));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Welcome, ");

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

        contentPanel.setBackground(new java.awt.Color(255, 236, 233));
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setPreferredSize(new java.awt.Dimension(700, 680));

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.7);
        jSplitPane1.setToolTipText("");
        jSplitPane1.setOneTouchExpandable(true);

        TableContents.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TableContents.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Title", "Description", "Status", "Created At", "Updated At"
            }
        ));
        TableContents.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        TableContents.setMaximumSize(new java.awt.Dimension(2147483647, 560));
        TableContents.setMinimumSize(new java.awt.Dimension(1200, 400));
        TableContents.setName(""); // NOI18N
        TableContents.setPreferredSize(new java.awt.Dimension(1200, 400));
        TableContents.getTableHeader().setResizingAllowed(false);
        TableContents.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(TableContents);

        jSplitPane1.setTopComponent(jScrollPane1);

        replypane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10), javax.swing.BorderFactory.createTitledBorder(null, "Conversation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 15)))); // NOI18N

        replyDisplay.setColumns(20);
        replyDisplay.setLineWrap(true);
        replyDisplay.setRows(5);
        jScrollPane2.setViewportView(replyDisplay);

        replyTextArea.setColumns(20);
        replyTextArea.setRows(5);
        jScrollPane3.setViewportView(replyTextArea);

        btnAddReply.setBackground(new java.awt.Color(0, 204, 255));
        btnAddReply.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddReply.setText("Send Message");
        btnAddReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddReplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout replypaneLayout = new javax.swing.GroupLayout(replypane);
        replypane.setLayout(replypaneLayout);
        replypaneLayout.setHorizontalGroup(
            replypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, replypaneLayout.createSequentialGroup()
                .addGroup(replypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(replypaneLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1023, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAddReply, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        replypaneLayout.setVerticalGroup(
            replypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replypaneLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(replypaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(btnAddReply, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jSplitPane1.setBottomComponent(replypane);

        javax.swing.GroupLayout ReplyPanelLayout = new javax.swing.GroupLayout(ReplyPanel);
        ReplyPanel.setLayout(ReplyPanelLayout);
        ReplyPanelLayout.setHorizontalGroup(
            ReplyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReplyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1255, Short.MAX_VALUE)
                .addContainerGap())
        );
        ReplyPanelLayout.setVerticalGroup(
            ReplyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReplyPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnRefresh.setBackground(new java.awt.Color(255, 102, 102));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnNewComplaint.setBackground(new java.awt.Color(51, 153, 255));
        btnNewComplaint.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnNewComplaint.setText("New Complaint");
        btnNewComplaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewComplaintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNewComplaint, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contentPanelLayout.createSequentialGroup()
                    .addComponent(ReplyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap(650, Short.MAX_VALUE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewComplaint, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contentPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ReplyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(69, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1272, Short.MAX_VALUE))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 571, Short.MAX_VALUE)
                        .addComponent(btnLogout)))
                .addGap(47, 47, 47))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogout))
                .addGap(7, 7, 7)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 746, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
         new LoginForm().setVisible(true);
    this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnNewComplaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewComplaintActionPerformed
        // TODO add your handling code here:
        new ComplaintForm(user).setVisible(true);
        loadComplaints(); // Refresh the list after form closes
    }//GEN-LAST:event_btnNewComplaintActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadComplaints();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnAddReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddReplyActionPerformed
   int selectedRow = TableContents.getSelectedRow();
    if (selectedRow == -1) {
        showError("Please select a complaint first");
        return;
    }
    
    String message = replyTextArea.getText().trim();
    if (message.isEmpty()) {
        showError("Please enter a reply message");
        return;
    }
    
    int complaintId = (int) TableContents.getValueAt(selectedRow, 0);
    
    try {
        Reply reply = new Reply(complaintId, user.getUserId(), message, false);
        
        if (replyDAO.addReply(reply)) {
            replyTextArea.setText("");
            // Refresh the complaint details to show the new reply
            showSelectedComplaintDetails();
            showMessage("Reply added successfully!");
        } else {
            showError("Failed to add reply");
        }
    } catch (Exception ex) {
        showError("Error adding reply: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_btnAddReplyActionPerformed
    private void showMessage(String message) {
    JOptionPane.showMessageDialog(this, 
        "<html><div style='text-align: center;'>" + message + "</div></html>", 
        "Success", 
        JOptionPane.INFORMATION_MESSAGE);
}
private void showError(String message) {
    JOptionPane.showMessageDialog(this, 
        "<html><div style='text-align: center;'><b>Error:</b><br>" + message + "</div></html>", 
        "Error", 
        JOptionPane.ERROR_MESSAGE);
}
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
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               // new UserDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel ReplyPanel;
    private javax.swing.JTable TableContents;
    private javax.swing.JButton btnAddReply;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNewComplaint;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea replyDisplay;
    private javax.swing.JTextArea replyTextArea;
    private javax.swing.JPanel replypane;
    // End of variables declaration//GEN-END:variables
}
