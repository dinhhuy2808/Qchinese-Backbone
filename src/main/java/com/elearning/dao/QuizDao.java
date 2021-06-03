package com.elearning.dao;

import java.util.List;

import com.elearning.model.QuestionDescription;

public interface QuizDao {
	public boolean updateResultForLessonQuiz(List<QuestionDescription> questions, int hsk, int lesson, String type);
	int countTests();
}
