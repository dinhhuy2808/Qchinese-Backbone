package com.elearning.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.elearning.constant.QuestionType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "result")
@Getter @Setter
public class Result {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer hsk;
	private Integer test;
	private Integer number;
	private String answer;
	private String part;
	private QuestionType type;
	
}
