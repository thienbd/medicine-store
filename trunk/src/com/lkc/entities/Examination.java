package com.lkc.entities;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Examination implements Serializable {

	private static final long serialVersionUID = 7786020825281989241L;

	@Id
	private long id;
	private String dianogsis;
	private Calendar examDate;
	@ManyToOne(fetch = FetchType.EAGER)
	private User doctor;
	private double examCost;

	public Examination(long id, String dianogsis, Calendar examDate, User doctor, double examCost) {
		this.id = id;
		this.dianogsis = dianogsis;
		this.examDate = examDate;
		this.doctor = doctor;
		this.examCost = examCost;
	}

	public Examination(long id) {
		this(id, "", null, null, 0);
	}

	public Examination() {
		this(0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDianogsis() {
		return dianogsis;
	}

	public void setDianogsis(String dianogsis) {
		this.dianogsis = dianogsis;
	}

	public Calendar getExamDate() {
		return examDate;
	}

	public void setExamDate(Calendar examDate) {
		this.examDate = examDate;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public double getExamCost() {
		return examCost;
	}

	public void setExamCost(double examCost) {
		this.examCost = examCost;
	}

}
