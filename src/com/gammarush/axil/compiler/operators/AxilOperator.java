package com.gammarush.axil.compiler.operators;

public class AxilOperator {
	
	private String symbol;
	private String methodName;
	private int priority;
	private int argsLength;
	
	public AxilOperator(String symbol, String methodName, int priority) {
		this.symbol = symbol;
		this.methodName = methodName;
		this.priority = priority;
		this.argsLength = 2;
	}
	
	public AxilOperator(String symbol, String methodName, int priority, int parameters) {
		this.symbol = symbol;
		this.methodName = methodName;
		this.priority = priority;
		this.argsLength = parameters;
	}
	
	public boolean equals(String string) {
		return this.symbol.equals(string);
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getArgsLength() {
		return argsLength;
	}
	
}
