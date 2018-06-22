package com.gammarush.axil.compiler.memory;

import com.gammarush.axil.memory.AxilType;

public class AxilConstant {
	
	private int address;
	private Object value;
	private AxilType type;
	
	public AxilConstant(int address, Object value, AxilType type) {
		this.address = address;
		this.value = value;
		this.type = type;
	}
	
	public int getAddress() {
		return address;
	}
	
	public Object getValue() {
		return value;
	}
	
	public AxilType getType() {
		return type;
	}

}
