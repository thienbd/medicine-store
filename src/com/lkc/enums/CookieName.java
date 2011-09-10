package com.lkc.enums;

public enum CookieName {
	START_LENGTH("s"), END_LENGTH("e"), UID("i"), LANGUAGE("lg"), PASSWORD("pd");

	String name;

	private CookieName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
