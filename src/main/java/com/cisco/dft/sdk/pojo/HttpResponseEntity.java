package com.cisco.dft.sdk.pojo;

/**
 * Represents the HTTPResponse message
 * @author sujmuthu
 * @version 1.0
 * @date January 22, 2015
 */
public class HttpResponseEntity {

	private int statusCode;
	private String entity;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

}
