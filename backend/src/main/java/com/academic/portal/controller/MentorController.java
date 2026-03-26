package com.academic.portal.controller;

import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.dto.ReviewRequest;
import com.academic.portal.dto.StudentAssignmentResponse;
import com.academic.portal.entity.User;
import com.academic.portal.enums.Role;
import com.academic.portal.service.MentorService;
import com.academic.portal.service.RequestUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
@CrossOrigin(origins = "*")
public class MentorController {
    private final MentorService mentorService;
    private final RequestUserService requestUserService;

    public MentorController(MentorService mentorService, RequestUserService requestUserService) {
        this.mentorService = mentorService;
        this.requestUserService = requestUserService;
    }

    @GetMapping("/students")
    public List<StudentAssignmentResponse> assignedStudents(@RequestHeader("X-Auth-Token") String token) {
        User mentor = requestUserService.requireRole(token, Role.MENTOR);
        return mentorService.getAssignedStudents(mentor);
    }

    @GetMapping("/documents")
    public List<DocumentResponse> assignedDocuments(@RequestHeader("X-Auth-Token") String token,
                                                    HttpServletRequest request) {
        User mentor = requestUserService.requireRole(token, Role.MENTOR);
        return mentorService.getAssignedDocuments(mentor, baseUrl(request));
    }

    @PutMapping("/review/{documentId}")
    public DocumentResponse review(@RequestHeader("X-Auth-Token") String token,
                                   @PathVariable Long documentId,
                                   @Valid @RequestBody ReviewRequest request,
                                   HttpServletRequest servletRequest) {
        User mentor = requestUserService.requireRole(token, Role.MENTOR);
        return mentorService.reviewDocument(mentor, documentId, request, baseUrl(servletRequest));
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
