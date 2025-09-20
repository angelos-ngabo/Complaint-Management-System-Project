package dao;

import model.Complaint;
import model.Reply;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    private ReplyDAO replyDAO;

    public ComplaintDAO() {
        this.replyDAO = new ReplyDAO();
    }
    
    public boolean addComplaint(Complaint complaint) throws SQLException {
        String sql = "INSERT INTO complaints (user_id, title, description, status, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, complaint.getUserId());
            stmt.setString(2, complaint.getTitle());
            stmt.setString(3, complaint.getDescription());
            stmt.setString(4, complaint.getStatus());
            stmt.setTimestamp(5, complaint.getCreatedAt());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        complaint.setComplaintId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    public List<Complaint> getComplaintsByUserId(int userId) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Complaint complaint = new Complaint(
                        rs.getInt("complaint_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    complaints.add(complaint);
                }
            }
        }
        return complaints;
    }
    
    public Complaint getComplaintById(int complaintId) throws SQLException {
        String sql = "SELECT * FROM complaints WHERE complaint_id = ?";
        
        try (Connection conn = DBConnection.getConnection();  // ✅ corrected here
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, complaintId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Complaint complaint = new Complaint(
                    rs.getInt("complaint_id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
                
                // Load replies for this complaint
                complaint.setReplies(replyDAO.getRepliesByComplaintId(complaintId));
                return complaint;
            }
        }
        return null;
    }
    
    public boolean updateComplaint(Complaint complaint) throws SQLException {
        String sql = "UPDATE complaints SET title = ?, description = ?, status = ?, updated_at = ? WHERE complaint_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, complaint.getTitle());
            stmt.setString(2, complaint.getDescription());
            stmt.setString(3, complaint.getStatus());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(5, complaint.getComplaintId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteComplaint(int complaintId) throws SQLException {
        String sql = "DELETE FROM complaints WHERE complaint_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, complaintId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Complaint> getAllComplaints() throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Complaint complaint = new Complaint(
                    rs.getInt("complaint_id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
                complaints.add(complaint);
            }
        }
        return complaints;
    }
    
    public boolean updateComplaintStatus(int complaintId, String status) throws SQLException {
        // Normalize the status value
        String normalizedStatus = status.trim();
        if (normalizedStatus.equalsIgnoreCase("in progress")) {
            normalizedStatus = "In Progress";
        } else if (normalizedStatus.equalsIgnoreCase("pending")) {
            normalizedStatus = "Pending";
        } else if (normalizedStatus.equalsIgnoreCase("resolved")) {
            normalizedStatus = "Resolved";
        }

        String sql = "UPDATE complaints SET status = ?, updated_at = ? WHERE complaint_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, normalizedStatus);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, complaintId);
            
            return stmt.executeUpdate() > 0;
        }
    }
}
