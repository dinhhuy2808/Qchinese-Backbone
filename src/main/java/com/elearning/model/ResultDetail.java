package com.elearning.model;

import java.util.List;

public class ResultDetail {
	private Integer hsk;
	private Integer test;
	private Integer score;
	private Double rate;
	private List<Answer> answers;
	private String listenRate;
	private String readRate;
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
	public Integer getTest() {
		return test;
	}
	public void setTest(Integer test) {
		this.test = test;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	public String getListenRate() {
		return listenRate;
	}
	public void setListenRate(String listenRate) {
		this.listenRate = listenRate;
	}
	public String getReadRate() {
		return readRate;
	}
	public void setReadRate(String readRate) {
		this.readRate = readRate;
	}
}
