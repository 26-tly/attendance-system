package com.example.attendance.jpa.repository;

import com.example.attendance.jpa.entity.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {
    
    List<CourseStudent> findByCourseId(Integer courseId);
    
    List<CourseStudent> findByStudentId(Long studentId);
    
    Optional<CourseStudent> findByCourseIdAndStudentId(Integer courseId, Long studentId);
    
    boolean existsByCourseIdAndStudentId(Integer courseId, Long studentId);
    
    void deleteByCourseIdAndStudentId(Integer courseId, Long studentId);
    
    @Query("SELECT cs FROM CourseStudent cs JOIN Student s ON cs.studentId = s.id " +
           "WHERE cs.courseId = :courseId")
    List<CourseStudent> findStudentsByCourseId(@Param("courseId") Integer courseId);
    
    @Query("SELECT COUNT(cs) FROM CourseStudent cs WHERE cs.courseId = :courseId")
    long countByCourseId(@Param("courseId") Integer courseId);
}
