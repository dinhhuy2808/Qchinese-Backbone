package com.elearning.jerseyguice.model;

import java.util.List;

public class LessonForInput {
	private String title;
	private List<LessonPartForInput> lessionParts;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<LessonPartForInput> getLessionParts() {
		return lessionParts;
	}
	public void setLessionParts(List<LessonPartForInput> lessionParts) {
		this.lessionParts = lessionParts;
	}
}
