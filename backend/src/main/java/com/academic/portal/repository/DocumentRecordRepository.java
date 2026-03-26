package com.academic.portal.repository;

import com.academic.portal.entity.DocumentRecord;
import com.academic.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRecordRepository extends JpaRepository<DocumentRecord, Long> {
    List<DocumentRecord> findByStudentOrderByUploadDateDesc(User student);
    List<DocumentRecord> findAllByOrderByUploadDateDesc();
}
