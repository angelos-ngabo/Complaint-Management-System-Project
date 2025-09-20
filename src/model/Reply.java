package model;

import java.sql.Timestamp;

public class Reply {
    private int replyId;
    private int complaintId;
    private int adminId;
    private String message;
    private boolean isAdminReply;
    private Timestamp createdAt;

    // Constructor
    public Reply(int replyId, int complaintId, int adminId, String message, boolean isAdminReply, Timestamp createdAt) {
        this.replyId = replyId;
        this.complaintId = complaintId;
        this.adminId = adminId;
        this.message = message;
        this.isAdminReply = isAdminReply;
        this.createdAt = createdAt;
    }

    // Overloaded constructor without replyId (for inserting new reply)
    public Reply(int complaintId, int adminId, String message, boolean isAdminReply) {
        this.complaintId = complaintId;
        this.adminId = adminId;
        this.message = message;
        this.isAdminReply = isAdminReply;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAdminReply() {
        return isAdminReply;
    }

    public void setAdminReply(boolean adminReply) {
        isAdminReply = adminReply;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
