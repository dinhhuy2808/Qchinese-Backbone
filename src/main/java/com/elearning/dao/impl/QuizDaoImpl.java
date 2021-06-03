package com.elearning.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.elearning.constant.QuestionType;
import com.elearning.dao.QuizDao;
import com.elearning.model.QuestionDescription;
import com.elearning.repository.ResultRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuizDaoImpl implements QuizDao {
	private final ResultRepository resultRepository;

	@Override
	public boolean updateResultForLessonQuiz(List<QuestionDescription> questions, int hsk, int lesson, String type) {
		return false;
	}

	@Override
	public int countTests() {
		return resultRepository.countDistincHskByType(QuestionType.TEST);
	}

}
