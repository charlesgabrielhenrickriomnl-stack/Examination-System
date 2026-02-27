package com.exam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exam.entity.OriginalProcessedPaper;

@Repository
public interface OriginalProcessedPaperRepository extends JpaRepository<OriginalProcessedPaper, Long> {
    Optional<OriginalProcessedPaper> findByExamId(String examId);
    List<OriginalProcessedPaper> findByTeacherEmailOrderByProcessedAtDesc(String teacherEmail);
}
