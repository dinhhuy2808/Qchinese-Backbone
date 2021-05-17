package com.elearning.jerseyguice.model;

import java.util.List;

public class Lesson {
	private String title;
	private List<LessonPart> lessionParts;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<LessonPart> getLessionParts() {
		return lessionParts;
	}
	public void setLessionParts(List<LessonPart> lessionParts) {
		this.lessionParts = lessionParts;
	}
	
	
}
