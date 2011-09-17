package com.lkc.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "examination_id", "medicine_id" }) })
public class ExaminationDetail implements Serializable {

	private static final long serialVersionUID = 6616132831299705349L;

	@Id
	private long id;
	@ManyToOne(fetch = FetchType.EAGER)
	private Examination examination;
	@ManyToOne(fetch = FetchType.EAGER)
	private Medicine medicine;
	private int quantity;
	private String usingGuide;

	public ExaminationDetail(long id, Examination examination, Medicine medicine, int quantity, String usingGuide) {
		this.id = id;
		this.examination = examination;
		this.medicine = medicine;
		this.quantity = quantity;
		this.usingGuide = usingGuide;
	}

	public ExaminationDetail(long id) {
		this(id, null, null, 0, "");
	}

	public ExaminationDetail() {
		this(0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Examination getExamination() {
		return examination;
	}

	public void setExamination(Examination examination) {
		this.examination = examination;
	}

	public Medicine getMedicine() {
		return medicine;
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUsingGuide() {
		return usingGuide;
	}

	public void setUsingGuide(String usingGuide) {
		this.usingGuide = usingGuide;
	}

}
