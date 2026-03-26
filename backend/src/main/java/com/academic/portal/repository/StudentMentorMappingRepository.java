package com.academic.portal.repository;

import com.academic.portal.entity.StudentMentorMapping;
import com.academic.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentMentorMappingRepository extends JpaRepository<StudentMentorMapping, Long> {
    Optional<StudentMentorMapping> findByStudent(User student);
    List<StudentMentorMapping> findByMentor(User mentor);
}
