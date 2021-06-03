package com.elearning.util;

public class Person {
	private String name[];
	private String location;

	public Person(String[] name, String location) {
		super();
		this.name = name;
		this.location = location;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", location=" + location + "]";
	}

}
