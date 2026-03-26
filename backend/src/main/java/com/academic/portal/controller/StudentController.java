package com.academic.portal.controller;

import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.entity.User;
import com.academic.portal.enums.Role;
import com.academic.portal.service.RequestUserService;
import com.academic.portal.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {
    private final StudentService studentService;
    private final RequestUserService requestUserService;

    public StudentController(StudentService studentService, RequestUserService requestUserService) {
        this.studentService = studentService;
        this.requestUserService = requestUserService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public DocumentResponse upload(@RequestHeader("X-Auth-Token") String token,
                                   @RequestParam("documentName") String documentName,
                                   @RequestParam("file") MultipartFile file,
                                   HttpServletRequest request) {
        User student = requestUserService.requireRole(token, Role.STUDENT);
        return studentService.uploadDocument(student, documentName, file, baseUrl(request));
    }

    @GetMapping("/documents")
    public List<DocumentResponse> myDocuments(@RequestHeader("X-Auth-Token") String token,
                                              HttpServletRequest request) {
        User student = requestUserService.requireRole(token, Role.STUDENT);
        return studentService.getStudentDocuments(student, baseUrl(request));
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
