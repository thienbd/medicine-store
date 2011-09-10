package com.lkc.entities;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Patient implements Serializable {

	private static final long serialVersionUID = -3656116537157842943L;

	@Id
	private long id;
	private String fullName;
	private Calendar dateOfBirth;
	private String address;

	public Patient(long id, String fullName, Calendar dateOfBirth, String address) {
		this.id = id;
		this.fullName = fullName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
	}

	public Patient(long id) {
		this(id, "", null, "");
	}

	public Patient() {
		this(0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Calendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Calendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
