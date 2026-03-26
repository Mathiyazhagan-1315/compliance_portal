package com.academic.portal.dto;

import com.academic.portal.entity.DocumentRecord;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class DocumentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String documentName;
    private String originalFileName;
    private String status;
    private String mentorComment;
    private String reviewedBy;
    private LocalDateTime uploadDate;
    private LocalDateTime reviewDate;
    private String downloadUrl;

    public static DocumentResponse from(DocumentRecord record, String baseUrl) {
        DocumentResponse response = new DocumentResponse();
        response.id = record.getId();
        response.studentId = record.getStudent().getId();
        response.studentName = record.getStudent().getName();
        response.documentName = record.getDocumentName();
        response.originalFileName = record.getOriginalFileName();
        response.status = record.getStatus().name();
        response.mentorComment = record.getMentorComment();
        response.reviewedBy = record.getReviewedBy() == null ? null : record.getReviewedBy().getName();
        response.uploadDate = record.getUploadDate();
        response.reviewDate = record.getReviewDate();
        response.downloadUrl = baseUrl + "/api/files/" + record.getId();
        return response;
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getDocumentName() { return documentName; }
    public String getOriginalFileName() { return originalFileName; }
    public String getStatus() { return status; }
    public String getMentorComment() { return mentorComment; }
    public String getReviewedBy() { return reviewedBy; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public LocalDateTime getReviewDate() { return reviewDate; }
    public String getDownloadUrl() { return downloadUrl; }
}
