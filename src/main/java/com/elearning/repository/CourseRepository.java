package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.elearning.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	long deleteByHsk(int hsk);

	long countByHskAndLesson(int hsk, String lesson);
	
	Course findByHskAndLesson(int hsk, String lesson);

	List<Course> findByHsk(int hsk);

	@Query(value = "select id, hsk,title, (select count(lesson) from course c1 where c1.hsk = c.hsk ) as lesson"
			+ " from course c group by hsk order by hsk", nativeQuery = true)
	List<Course> getAllCourses();
}
