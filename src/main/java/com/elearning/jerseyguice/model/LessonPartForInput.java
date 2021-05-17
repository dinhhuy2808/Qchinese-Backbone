package com.elearning.jerseyguice.model;

public class LessonPartForInput implements Cloneable {
	private String part;
	String content;
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
