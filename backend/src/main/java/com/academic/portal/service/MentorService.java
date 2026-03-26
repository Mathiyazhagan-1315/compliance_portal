package com.academic.portal.service;

import com.academic.portal.dto.DocumentResponse;
import com.academic.portal.dto.ReviewRequest;
import com.academic.portal.dto.StudentAssignmentResponse;
import com.academic.portal.entity.DocumentRecord;
import com.academic.portal.entity.StudentMentorMapping;
import com.academic.portal.entity.User;
import com.academic.portal.enums.DocumentStatus;
import com.academic.portal.enums.Role;
import com.academic.portal.repository.DocumentRecordRepository;
import com.academic.portal.repository.StudentMentorMappingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MentorService {
    private final StudentMentorMappingRepository mappingRepository;
    private final DocumentRecordRepository documentRecordRepository;

    public MentorService(StudentMentorMappingRepository mappingRepository, DocumentRecordRepository documentRecordRepository) {
        this.mappingRepository = mappingRepository;
        this.documentRecordRepository = documentRecordRepository;
    }

    public List<StudentAssignmentResponse> getAssignedStudents(User mentor) {
        if (mentor.getRole() != Role.MENTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only mentors can access this");
        }
        return mappingRepository.findByMentor(mentor).stream()
                .map(m -> new StudentAssignmentResponse(m.getStudent().getId(), m.getStudent().getName(), m.getStudent().getEmail()))
                .toList();
    }

    public List<DocumentResponse> getAssignedDocuments(User mentor, String baseUrl) {
        Set<Long> studentIds = mappingRepository.findByMentor(mentor).stream()
                .map(m -> m.getStudent().getId()).collect(Collectors.toSet());

        return documentRecordRepository.findAllByOrderByUploadDateDesc().stream()
                .filter(d -> studentIds.contains(d.getStudent().getId()))
                .map(d -> DocumentResponse.from(d, baseUrl))
                .toList();
    }

    public DocumentResponse reviewDocument(User mentor, Long documentId, ReviewRequest request, String baseUrl) {
        DocumentRecord record = documentRecordRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        StudentMentorMapping mapping = mappingRepository.findByStudent(record.getStudent())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not assigned to a mentor"));

        if (!mapping.getMentor().getId().equals(mentor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not assigned to this student" );
        }

        DocumentStatus status = DocumentStatus.valueOf(request.getStatus().toUpperCase());
        if (status == DocumentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review status");
        }

        record.setStatus(status);
        record.setMentorComment(request.getMentorComment());
        record.setReviewedBy(mentor);
        record.setReviewDate(LocalDateTime.now());

        return DocumentResponse.from(documentRecordRepository.save(record), baseUrl);
    }
}
