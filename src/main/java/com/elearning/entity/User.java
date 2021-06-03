package com.elearning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter @Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;
	private String email;
	private Integer dob;
	private String phone;
	private String name;
	private Integer create_time;
	@Column(name = "accountType")
	private Integer accountType;
	private String password;
	private String image;
	private String username;
	private String address;
	private String gender;
	private String avatar;
	private String userkey;
}      
