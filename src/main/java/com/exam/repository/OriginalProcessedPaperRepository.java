package com.exam.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exam.entity.OriginalProcessedPaper;

@Repository
public interface OriginalProcessedPaperRepository extends JpaRepository<OriginalProcessedPaper, Long> {
    Optional<OriginalProcessedPaper> findByExamId(String examId);
    List<OriginalProcessedPaper> findByTeacherEmailOrderByProcessedAtDesc(String teacherEmail);
    List<OriginalProcessedPaper> findByTeacherEmailIgnoreCaseOrderByProcessedAtDesc(String teacherEmail);
    Page<OriginalProcessedPaper> findByTeacherEmailIgnoreCaseOrderByProcessedAtDesc(String teacherEmail, Pageable pageable);
    Page<OriginalProcessedPaper> findByTeacherEmailIgnoreCaseAndExamNameContainingIgnoreCaseOrderByProcessedAtDesc(
        String teacherEmail,
        String examName,
        Pageable pageable
    );
    List<OriginalProcessedPaper> findByDepartmentNameIgnoreCaseOrderByProcessedAtDesc(String departmentName);
    Page<OriginalProcessedPaper> findByDepartmentNameIgnoreCaseAndTeacherEmailNotIgnoreCaseOrderByProcessedAtDesc(
        String departmentName,
        String teacherEmail,
        Pageable pageable
    );
    Page<OriginalProcessedPaper> findByDepartmentNameIgnoreCaseAndTeacherEmailNotIgnoreCaseAndTeacherPullSharedTrueOrderByProcessedAtDesc(
        String departmentName,
        String teacherEmail,
        Pageable pageable
    );
    Page<OriginalProcessedPaper> findByDepartmentNameIgnoreCaseAndTeacherEmailNotIgnoreCaseAndExamNameContainingIgnoreCaseOrderByProcessedAtDesc(
        String departmentName,
        String teacherEmail,
        String examName,
        Pageable pageable
    );
    Page<OriginalProcessedPaper> findByDepartmentNameIgnoreCaseAndTeacherEmailNotIgnoreCaseAndTeacherPullSharedTrueAndExamNameContainingIgnoreCaseOrderByProcessedAtDesc(
        String departmentName,
        String teacherEmail,
        String examName,
        Pageable pageable
    );

    @Query("""
        SELECT p
        FROM OriginalProcessedPaper p
        WHERE LOWER(p.departmentName) = LOWER(:departmentName)
          AND LOWER(p.teacherEmail) <> LOWER(:teacherEmail)
          AND p.teacherPullShared = true
          AND (
                (UPPER(p.sharingScope) = 'PROGRAM' AND :hasProgramScope = true AND LOWER(p.sharedProgramName) IN :programNames)
                OR (UPPER(p.sharingScope) = 'TEACHER' AND LOWER(p.sharedTeacherEmail) = LOWER(:teacherEmail))
          )
        ORDER BY p.processedAt DESC
        """)
    Page<OriginalProcessedPaper> findVisibleSharedPapersForTeacher(
        @Param("departmentName") String departmentName,
        @Param("teacherEmail") String teacherEmail,
        @Param("hasProgramScope") boolean hasProgramScope,
        @Param("programNames") List<String> programNames,
        Pageable pageable
    );

    @Query("""
        SELECT p
        FROM OriginalProcessedPaper p
        WHERE LOWER(p.departmentName) = LOWER(:departmentName)
          AND LOWER(p.teacherEmail) <> LOWER(:teacherEmail)
          AND p.teacherPullShared = true
          AND LOWER(p.examName) LIKE LOWER(CONCAT('%', :examName, '%'))
          AND (
                (UPPER(p.sharingScope) = 'PROGRAM' AND :hasProgramScope = true AND LOWER(p.sharedProgramName) IN :programNames)
                OR (UPPER(p.sharingScope) = 'TEACHER' AND LOWER(p.sharedTeacherEmail) = LOWER(:teacherEmail))
          )
        ORDER BY p.processedAt DESC
        """)
    Page<OriginalProcessedPaper> findVisibleSharedPapersForTeacherWithSearch(
        @Param("departmentName") String departmentName,
        @Param("teacherEmail") String teacherEmail,
        @Param("examName") String examName,
        @Param("hasProgramScope") boolean hasProgramScope,
        @Param("programNames") List<String> programNames,
        Pageable pageable
    );

    Page<OriginalProcessedPaper> findByQuestionCountIsNull(Pageable pageable);
}
