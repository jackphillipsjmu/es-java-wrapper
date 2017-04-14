package com.es.rest.wrapper.enumeration;

public enum RefreshEnum {
	TRUE("true"),
    WAIT("wait_for"),
    FALSE("false");
	
	private String value;
	
	RefreshEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
