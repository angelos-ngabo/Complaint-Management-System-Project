package dao;

import model.Reply;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyDAO {

    // Add a reply
    public boolean addReply(Reply reply) throws SQLException {
        String sql = "INSERT INTO replies (complaint_id, admin_id, message, is_admin_reply, created_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reply.getComplaintId());
            stmt.setInt(2, reply.getAdminId());
            stmt.setString(3, reply.getMessage());
            stmt.setBoolean(4, reply.isAdminReply());
            stmt.setTimestamp(5, reply.getCreatedAt());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reply.setReplyId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // Get replies by complaint id
    public List<Reply> getRepliesByComplaintId(int complaintId) throws SQLException {
        List<Reply> replies = new ArrayList<>();
        String sql = "SELECT * FROM replies WHERE complaint_id = ? ORDER BY created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, complaintId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reply reply = new Reply(
                    rs.getInt("reply_id"),
                    rs.getInt("complaint_id"),
                    rs.getInt("admin_id"),
                    rs.getString("message"),
                    rs.getBoolean("is_admin_reply"),
                    rs.getTimestamp("created_at")
                );
                replies.add(reply);
            }
        }
        return replies;
    }

    // Delete replies for a specific complaint
    public boolean deleteRepliesByComplaint(int complaintId) throws SQLException {
        String sql = "DELETE FROM replies WHERE complaint_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, complaintId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete a specific reply
    public boolean deleteReply(int replyId) throws SQLException {
        String sql = "DELETE FROM replies WHERE reply_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, replyId);
            return stmt.executeUpdate() > 0;
        }
    }
}
