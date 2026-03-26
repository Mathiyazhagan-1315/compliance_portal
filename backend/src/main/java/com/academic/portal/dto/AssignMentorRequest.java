package com.academic.portal.dto;

import jakarta.validation.constraints.NotNull;

public class AssignMentorRequest {
    @NotNull
    private Long studentId;
    @NotNull
    private Long mentorId;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
}
