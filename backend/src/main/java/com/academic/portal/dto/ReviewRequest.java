package com.academic.portal.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequest {
    @NotBlank
    private String status;
    private String mentorComment;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMentorComment() { return mentorComment; }
    public void setMentorComment(String mentorComment) { this.mentorComment = mentorComment; }
}
