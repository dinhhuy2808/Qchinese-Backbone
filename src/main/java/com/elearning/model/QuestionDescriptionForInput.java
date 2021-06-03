package com.elearning.model;

import java.util.List;

import com.elearning.constant.Category;

public class QuestionDescriptionForInput implements Cloneable {
	String type;
	String number;
	List<String> contents;
	Category category;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public List<String> getContents() {
		return contents;
	}
	public void setContents(List<String> contents) {
		this.contents = contents;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
}
