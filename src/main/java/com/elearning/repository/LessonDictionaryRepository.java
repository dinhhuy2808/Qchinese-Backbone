package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.LessonDictionary;

@Repository
public interface LessonDictionaryRepository extends JpaRepository<LessonDictionary, Long> {
	List<LessonDictionary> findAllByHskAndLessonAndPartAndStandartOrderByOrderAsc(int hsk, String lesson, String part, int standart);
}
