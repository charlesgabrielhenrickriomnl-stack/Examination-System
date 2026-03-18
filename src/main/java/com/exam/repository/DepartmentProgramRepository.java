package com.exam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exam.entity.DepartmentProgram;

@Repository
public interface DepartmentProgramRepository extends JpaRepository<DepartmentProgram, Long> {
    List<DepartmentProgram> findByDepartmentNameIgnoreCaseOrderByProgramNameAsc(String departmentName);
}
