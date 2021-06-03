package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.UserResult;

@Repository
public interface UserResultRepository extends JpaRepository<UserResult, Long> {
	List<UserResult> findByUserIdAndHskAndTestLessonAndResultType(Long userId, int hsk, int testLesson,
			String resultType);

	List<UserResult> findByUserIdAndHskAndTestLessonAndResultTypeOrderByIdDesc(Long userId, int hsk, int testLesson,
			String resultType);
	
	List<UserResult> findByUserIdAndHskAndTestLessonAndResultTypeOrderByIdAsc(Long userId, int hsk, int testLesson,
			String resultType);
	
	List<UserResult> findByUserIdAndResultTypeOrderByIdDesc(Long userId, String resultType);
}
