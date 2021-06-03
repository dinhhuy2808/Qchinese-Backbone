package com.elearning.model;

public class GetCourseDicRequest {
	private int hsk;
	private String lesson;
	private String part;
	public int getHsk() {
		return hsk;
	}
	public void setHsk(int hsk) {
		this.hsk = hsk;
	}
	public String getLesson() {
		return lesson;
	}
	public void setLesson(String lesson) {
		this.lesson = lesson;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
}
