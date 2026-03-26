package com.academic.portal.controller;

import com.academic.portal.entity.DocumentRecord;
import com.academic.portal.entity.StudentMentorMapping;
import com.academic.portal.entity.User;
import com.academic.portal.enums.Role;
import com.academic.portal.repository.DocumentRecordRepository;
import com.academic.portal.repository.StudentMentorMappingRepository;
import com.academic.portal.service.RequestUserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {
    private final DocumentRecordRepository documentRecordRepository;
    private final StudentMentorMappingRepository mappingRepository;
    private final RequestUserService requestUserService;

    public FileController(DocumentRecordRepository documentRecordRepository,
                          StudentMentorMappingRepository mappingRepository,
                          RequestUserService requestUserService) {
        this.documentRecordRepository = documentRecordRepository;
        this.mappingRepository = mappingRepository;
        this.requestUserService = requestUserService;
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Resource> download(@RequestHeader("X-Auth-Token") String token,
                                             @PathVariable Long documentId) {
        User user = requestUserService.requireUser(token);
        DocumentRecord record = documentRecordRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        boolean allowed = false;
        if (user.getRole() == Role.ADMIN) allowed = true;
        if (user.getRole() == Role.STUDENT && record.getStudent().getId().equals(user.getId())) allowed = true;
        if (user.getRole() == Role.MENTOR) {
            StudentMentorMapping mapping = mappingRepository.findByStudent(record.getStudent()).orElse(null);
            allowed = mapping != null && mapping.getMentor().getId().equals(user.getId());
        }
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        File file = new File(record.getFilePath());
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stored file not found");
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + record.getOriginalFileName() + "\"")
                .body(resource);
    }
}
