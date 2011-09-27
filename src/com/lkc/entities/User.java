package com.lkc.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "MyUser")
public class User implements Serializable {
	private static final long serialVersionUID = 2019084101580715360L;

	@Id
	private long id;
	private String userName;
	private String realName;
	private String password;

	private boolean admin;

	private String language = "vn";

	public User() {
		this(0);
	}

	public User(long id) {
		this(id, "", "");
	}

	public User(long id, String userName, String password) {
		this(id, userName, "", password);
	}

	public User(long id, String userName, String realName, String password) {
		this.id = id;
		this.userName = userName;
		this.realName = realName;
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof User)) {
			User that = (User) obj;
			return this.id == that.id;
		}
		return false;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean isAdmin) {
		this.admin = isAdmin;
	}

}
