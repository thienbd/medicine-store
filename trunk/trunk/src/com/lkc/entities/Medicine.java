package com.lkc.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Medicine implements Serializable {

	private static final long serialVersionUID = -8926800765247561921L;

	@Id
	private long id;
	private String name;
	private double price;

	public Medicine(long id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public Medicine(long id) {
		this(id, "", 0);
	}

	public Medicine() {
		this(0);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
