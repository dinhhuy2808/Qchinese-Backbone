package com.elearning.jerseyguice.model;

import java.util.List;

public class QuizResultResponse {
	private ResultDetail resultDetail;
	private List<QuestionDescription> questionDescription;
	public ResultDetail getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(ResultDetail resultDetail) {
		this.resultDetail = resultDetail;
	}
	public List<QuestionDescription> getQuestionDescription() {
		return questionDescription;
	}
	public void setQuestionDescription(List<QuestionDescription> questionDescription) {
		this.questionDescription = questionDescription;
	}
	
	
}
