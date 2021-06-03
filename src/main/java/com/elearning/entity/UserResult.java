package com.elearning.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "userresult")
@Getter @Setter @Data
public class UserResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private Integer hsk;
	private Integer testLesson;
	private Long totalScore;
	private Long totalTime;
	private Long totalListenScore;
	private Long totalListenTime;
	private Long totalReadingScore;
	private Long totalReadingTime;
	private String resultDetail;
	private Integer wordAmount;
	private String wordDetail;
	private String resultType;
	private LocalDateTime submitDate;
}
