package com.elearning.jerseyguice.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubjectHolderForJWT {
	private Long user_id;
	private String phone;
	private String name;
	private Integer loginMethod;//1: normal 2:facebook 3:google
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLoginMethod() {
		return loginMethod;
	}
	public void setLoginMethod(Integer loginMethod) {
		this.loginMethod = loginMethod;
	}
	
	
}      
