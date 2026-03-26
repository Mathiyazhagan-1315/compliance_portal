package com.academic.portal.controller;

import com.academic.portal.dto.AssignMentorRequest;
import com.academic.portal.dto.CreateUserRequest;
import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.dto.UserResponse;
import com.academic.portal.enums.Role;
import com.academic.portal.service.AdminService;
import com.academic.portal.service.RequestUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    private final AdminService adminService;
    private final RequestUserService requestUserService;

    public AdminController(AdminService adminService, RequestUserService requestUserService) {
        this.adminService = adminService;
        this.requestUserService = requestUserService;
    }

    @PostMapping("/users")
    public UserResponse createUser(@RequestHeader("X-Auth-Token") String token,
                                   @Valid @RequestBody CreateUserRequest request) {
        requestUserService.requireRole(token, Role.ADMIN);
        return adminService.createUser(request);
    }

    @PostMapping("/assign-mentor")
    public ResponseEntity<String> assignMentor(@RequestHeader("X-Auth-Token") String token,
                                               @Valid @RequestBody AssignMentorRequest request) {
        requestUserService.requireRole(token, Role.ADMIN);
        adminService.assignMentor(request);
        return ResponseEntity.ok("Mentor assigned successfully");
    }

    @GetMapping("/users/{role}")
    public List<UserResponse> getUsersByRole(@RequestHeader("X-Auth-Token") String token,
                                             @PathVariable String role) {
        requestUserService.requireRole(token, Role.ADMIN);
        return adminService.getUsersByRole(role);
    }

    @GetMapping("/documents")
    public List<DocumentResponse> getAllDocuments(@RequestHeader("X-Auth-Token") String token,
                                                  HttpServletRequest request) {
        requestUserService.requireRole(token, Role.ADMIN);
        return adminService.getAllDocuments(baseUrl(request));
    }

    private String baseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
