package com.gammarush.axil.methods;

import com.gammarush.axil.memory.AxilMemory;

public class AxilMethod {
	
	private int id;
	private String name;
	private int argsLength;
	private AxilMethodInterface method;
	
	public AxilMethod(int id, String name, int argsLength, AxilMethodInterface method) {
		this.id = id;
		this.name = name;
		this.argsLength = argsLength;
		this.method = method;
	}
	
	public int execute(int[] args, AxilMemory memory) {
		return method.execute(args, memory);
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArgsLength() {
		return argsLength;
	}

}
