package com.elearning.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lessondictionary")
@Getter @Setter
public class LessonDictionary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String hantu;
	private String pinyin;
	private String nghia1;
	private String hanviet;
	private Integer hsk;
	private String lesson;
	private String part;
	private Integer standart;
	private Integer popular;
	private Long refid;
	private Integer order;
}
