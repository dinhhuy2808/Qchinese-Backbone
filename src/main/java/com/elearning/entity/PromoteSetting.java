package com.elearning.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "promotesetting")
@Getter @Setter
public class PromoteSetting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer hsk;
	private Integer wordAmount;
	private Integer avgExerciseScore;
	private Integer promoteChain;
	private Integer scorePerTest;
	private Integer targetRankingId;
	private Integer factor;
	private Integer tests;
}
