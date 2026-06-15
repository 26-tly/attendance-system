package com.example.attendance.jpa.service.impl;

import com.example.attendance.common.Result;
import com.example.attendance.jpa.entity.Course;
import com.example.attendance.jpa.repository.AttendanceRepository;
import com.example.attendance.jpa.repository.CourseRepository;
import com.example.attendance.jpa.service.JpaCourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JpaCourseServiceImpl implements JpaCourseService {

    private static final Logger log = LoggerFactory.getLogger(JpaCourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final JdbcTemplate jdbcTemplate;

    public JpaCourseServiceImpl(CourseRepository courseRepository, AttendanceRepository attendanceRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Course> addCourse(Course course) {
        Course saved = courseRepository.save(course);
        return Result.success(saved);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Course> updateCourse(Course course) {
        if (!courseRepository.existsById(course.getCourseId())) {
            return Result.error("课程不存在");
        }
        Course updated = courseRepository.save(course);
        return Result.success(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteCourse(Integer courseId) {
        log.info("尝试删除课程, ID: {}", courseId);
        
        if (!courseRepository.existsById(courseId)) {
            log.warn("课程不存在, ID: {}", courseId);
            return Result.error("课程不存在");
        }
        
        try {
            long attendanceCount = attendanceRepository.countByCourseId(courseId);
            log.info("关联的考勤记录数: {}", attendanceCount);
            
            if (attendanceCount > 0) {
                jdbcTemplate.update("DELETE FROM attendance WHERE course_id = ?", courseId);
                log.info("已删除关联的考勤记录");
            }
            
            int selectionDeleted = jdbcTemplate.update("DELETE FROM course_selection WHERE course_id = ?", courseId);
            log.info("已删除选课记录数: {}", selectionDeleted);
            
            int courseDeleted = jdbcTemplate.update("DELETE FROM course WHERE course_id = ?", courseId);
            log.info("已删除课程记录数: {}", courseDeleted);
            
            if (courseDeleted == 0) {
                log.warn("删除课程失败，未找到匹配记录, ID: {}", courseId);
                return Result.error("删除课程失败，未找到匹配记录");
            }
            
            log.info("课程删除成功, ID: {}", courseId);
            return Result.success();
            
        } catch (DataIntegrityViolationException e) {
            log.error("数据完整性异常: {}", e.getMessage());
            return Result.error("删除失败：该课程可能被其他数据引用");
        } catch (Exception e) {
            log.error("删除异常: {}", e.getMessage(), e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchDeleteCourses(List<Integer> courseIds) {
        log.info("尝试批量删除课程, IDs: {}", courseIds);
        
        if (courseIds == null || courseIds.isEmpty()) {
            log.warn("未选择任何课程");
            return Result.error("请选择要删除的课程");
        }
        
        try {
            int deletedCount = 0;
            for (Integer courseId : courseIds) {
                if (courseRepository.existsById(courseId)) {
                    long attendanceCount = attendanceRepository.countByCourseId(courseId);
                    if (attendanceCount > 0) {
                        jdbcTemplate.update("DELETE FROM attendance WHERE course_id = ?", courseId);
                    }
                    jdbcTemplate.update("DELETE FROM course_selection WHERE course_id = ?", courseId);
                    jdbcTemplate.update("DELETE FROM course WHERE course_id = ?", courseId);
                    deletedCount++;
                }
            }
            
            log.info("批量删除完成，成功删除 {} 门课程", deletedCount);
            return Result.success(deletedCount);
            
        } catch (DataIntegrityViolationException e) {
            log.error("数据完整性异常: {}", e.getMessage());
            return Result.error("删除失败：部分课程可能被其他数据引用");
        } catch (Exception e) {
            log.error("批量删除异常: {}", e.getMessage(), e);
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @Override
    public Course getById(Integer courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Page<Course> getCourses(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return courseRepository.findAll(pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }
}