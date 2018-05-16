package com.gammarush.axil.methods;

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
	
	public int execute(int[] args, Object[] storage) {
		return method.execute(args, storage);
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
