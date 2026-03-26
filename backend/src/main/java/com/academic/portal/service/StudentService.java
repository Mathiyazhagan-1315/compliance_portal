package com.academic.portal.service;

import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.entity.DocumentRecord;
import com.academic.portal.entity.User;
import com.academic.portal.enums.DocumentStatus;
import com.academic.portal.enums.Role;
import com.academic.portal.repository.DocumentRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StudentService {
    private final DocumentRecordRepository documentRecordRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public StudentService(DocumentRecordRepository documentRecordRepository) {
        this.documentRecordRepository = documentRecordRepository;
    }

    public DocumentResponse uploadDocument(User student, String documentName, MultipartFile file, String baseUrl) {
        if (student.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can upload");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        String originalName = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
        String safeName = UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Path target = uploadPath.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            DocumentRecord record = new DocumentRecord();
            record.setStudent(student);
            record.setDocumentName(documentName);
            record.setOriginalFileName(originalName);
            record.setFilePath(target.toAbsolutePath().toString());
            record.setStatus(DocumentStatus.PENDING);
            record.setUploadDate(LocalDateTime.now());

            return DocumentResponse.from(documentRecordRepository.save(record), baseUrl);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file");
        }
    }

    public List<DocumentResponse> getStudentDocuments(User student, String baseUrl) {
        return documentRecordRepository.findByStudentOrderByUploadDateDesc(student)
                .stream().map(d -> DocumentResponse.from(d, baseUrl)).toList();
    }
}
