package com.academic.portal.dto;

public class StudentAssignmentResponse {
    private Long studentId;
    private String studentName;
    private String studentEmail;

    public StudentAssignmentResponse(Long studentId, String studentName, String studentEmail) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public Long getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
}
