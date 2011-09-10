package com.lkc.enums;

public enum UserType {
	GUEST(0);
	long id;

	private UserType(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return super.toString() + ": " + id;
	}
}
