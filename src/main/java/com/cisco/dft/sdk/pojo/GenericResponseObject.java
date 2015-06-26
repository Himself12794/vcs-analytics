package com.cisco.dft.sdk.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GenericResponseObject {
	
	private String param1;
	private String param2;
	
	public GenericResponseObject(){
		
	}
	
	public GenericResponseObject(String param1, String param2) {
		super();
		this.param1 = param1;
		this.param2 = param2;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

    @Override
    public String toString() {
        return "GenericResponseObject{" +
                "param1='" + param1 + '\'' +
                ", param2='" + param2 + '\'' +
                '}';
    }
}
