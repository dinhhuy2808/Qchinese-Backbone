package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.constant.QuestionType;
import com.elearning.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
	List<Result> findByHskAndTestAndType(int hsk, int test, String type);
	long deleteByHskAndType(int hsk, String type);
	Integer countDistincHskByType(QuestionType type);
}
