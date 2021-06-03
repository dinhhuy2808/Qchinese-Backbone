package com.elearning.model;

import java.util.List;

public class QuizResultResponse {
	private ResultDetail resultDetail;
	private List<QuestionDescription> questionDescription;
	private String submitDate;
	private boolean isPromote;
	private long time;

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
	public String getSubmitDate(){
		return submitDate;
	}
	public void setSubmitDate(String submitDate){
		this.submitDate = submitDate;
	}
	public boolean getIsPromote() {
		return isPromote;
	}
	public void setIsPromote(boolean isPromote) {
		this.isPromote = isPromote;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
