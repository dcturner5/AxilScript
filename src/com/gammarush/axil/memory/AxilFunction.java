package com.gammarush.axil.memory;

public class AxilFunction {
	
	private String name;
	private int address;
	private int returnAddress;
	private int[] argAddresses;
	
	public AxilFunction(String name, int address, int returnAddress, int[] argAddresses) {
		this.name = name;
		this.address = address;
		this.returnAddress = returnAddress;
		this.argAddresses = argAddresses;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAddress() {
		return address;
	}
	
	public int getReturnAddress() {
		return returnAddress;
	}
	
	public int[] getArgAddresses() {
		return argAddresses;
	}

}
