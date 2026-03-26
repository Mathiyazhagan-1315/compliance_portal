package com.academic.portal.service;

import com.academic.portal.dto.AssignMentorRequest;
import com.academic.portal.dto.CreateUserRequest;
import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.dto.UserResponse;
import com.academic.portal.entity.StudentMentorMapping;
import com.academic.portal.entity.User;
import com.academic.portal.enums.Role;
import com.academic.portal.repository.DocumentRecordRepository;
import com.academic.portal.repository.StudentMentorMappingRepository;
import com.academic.portal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final StudentMentorMappingRepository mappingRepository;
    private final DocumentRecordRepository documentRecordRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, StudentMentorMappingRepository mappingRepository,
                        DocumentRecordRepository documentRecordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mappingRepository = mappingRepository;
        this.documentRecordRepository = documentRecordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        });

        Role role = Role.valueOf(request.getRole().toUpperCase());
        if (role == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin can only be created manually");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public void assignMentor(AssignMentorRequest request) {
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        User mentor = userRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mentor not found"));

        if (student.getRole() != Role.STUDENT || mentor.getRole() != Role.MENTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role mapping");
        }

        StudentMentorMapping mapping = mappingRepository.findByStudent(student).orElse(new StudentMentorMapping());
        mapping.setStudent(student);
        mapping.setMentor(mentor);
        mappingRepository.save(mapping);
    }

    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role.toUpperCase()))
                .stream().map(u -> new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name()))
                .toList();
    }

    public List<DocumentResponse> getAllDocuments(String baseUrl) {
        return documentRecordRepository.findAllByOrderByUploadDateDesc()
                .stream().map(d -> DocumentResponse.from(d, baseUrl)).toList();
    }
}
