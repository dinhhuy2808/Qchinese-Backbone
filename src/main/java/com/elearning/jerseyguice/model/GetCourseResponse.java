package com.elearning.jerseyguice.model;

public class GetCourseResponse {
	private String previousLesson;
	private String nextLesson;
	private Lesson lesson;
	private LessonForInput lessonForInput;
	public String getPreviousLesson() {
		return previousLesson;
	}
	public void setPreviousLesson(String previousLesson) {
		this.previousLesson = previousLesson;
	}
	public String getNextLesson() {
		return nextLesson;
	}
	public void setNextLesson(String nextLesson) {
		this.nextLesson = nextLesson;
	}
	public Lesson getLesson() {
		return lesson;
	}
	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}
	public LessonForInput getLessonForInput() {
		return lessonForInput;
	}
	public void setLessonForInput(LessonForInput lessonForInput) {
		this.lessonForInput = lessonForInput;
	}
}
