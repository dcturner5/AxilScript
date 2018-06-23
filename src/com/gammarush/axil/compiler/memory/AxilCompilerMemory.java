package com.gammarush.axil.compiler.memory;

import java.util.ArrayList;
import java.util.HashMap;

import com.gammarush.axil.memory.AxilFunction;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.memory.AxilType;

public class AxilCompilerMemory {
	
	private HashMap<String, Integer> addressMap = new HashMap<String, Integer>();
	private ArrayList<AxilConstant> constants = new ArrayList<AxilConstant>();
	
	private HashMap<String, AxilFunction> functionMap = new HashMap<String, AxilFunction>();
	private ArrayList<AxilFunction> functions = new ArrayList<AxilFunction>();
	
	private int size = 0;
	
	public int get(String name) {
		//temporary memory marked with :address
		if(name.charAt(0) == ':') {
			return parseInt(name.substring(1));
		}
		else if(addressMap.containsKey(name)) {
			return addressMap.get(name);
		}
		else {
			AxilType type = parseType(name);
			if(type == AxilType.BOOLEAN) {
				int address = set(name, parseBoolean(name));
				return address;
			}
			else if(type == AxilType.FLOAT) {
				int address = set(name, parseFloat(name));
				return address;
			}
			else if(type == AxilType.INT) {
				int address = set(name, parseInt(name));
				return address;
			}
			else if(type == AxilType.STRING) {
				int address = set(name, parseString(name));
				return address;
			}
			else {
				int address = set(name);
				return address;
			}
		}
	}
	
	public AxilFunction getFunction(String name) {
		return functionMap.get(name);
	}
	
	public ArrayList<AxilConstant> getConstants() {
		return constants;
	}
	
	public ArrayList<AxilFunction> getFunctions() {
		return functions;
	}
	
	//reserves this memory space for a future operation
	public int reserve() {
		int address = size;
		size++;
		return address;
	}
	
	public int set(String name) {
		int address = size;
		addressMap.put(name, address);
		size++;
		return address;
	}
	
	public int set(String name, boolean value) {
		int address = set(name);
		constants.add(new AxilConstant(address, value, AxilType.BOOLEAN));
		return address;
	}
	
	public int set(String name, float value) {
		int address = set(name);
		constants.add(new AxilConstant(address, value, AxilType.FLOAT));
		return address;
	}
	
	public int set(String name, int value) {
		int address = set(name);
		constants.add(new AxilConstant(address, value, AxilType.INT));
		return address;
	}
	
	public int set(String name, String value) {
		int address = set(name);
		constants.add(new AxilConstant(address, value, AxilType.STRING));
		return address;
	}
	
	public void setFunction(AxilFunction function) {
		functionMap.put(function.getName(), function);
		functions.add(function);
	}
	
	private boolean parseBoolean(String string) {
		if(string.equals("true")) return true;
		else return false;
	}
	
	private float parseFloat(String string) {
		return Float.valueOf(string.trim()).floatValue();
	}
	
	private int parseInt(String string) {
		return Integer.valueOf(string.trim()).intValue();
	}
	
	private String parseString(String string) {
		return string.substring(1, string.length() - 1);
	}
	
	private AxilType parseType(String string) {
		char first = string.charAt(0);
		char last = string.charAt(string.length() - 1);
		if(first == '\"' && last == '\"') {
			return AxilType.STRING;
		}
		else if(string.equals("true") || string.equals("false")) {
			return AxilType.BOOLEAN;
		}
		else {
			if(string.indexOf('.') != -1) {
				try {
					Float.valueOf(string.trim()).floatValue();
					return AxilType.FLOAT;
				}
				catch (NumberFormatException e) {
					return AxilType.UNDEFINED;
				}
			}
			else {
				try {
					Integer.valueOf(string.trim()).intValue();
					return AxilType.INT;
				}
				catch (NumberFormatException e) {
					return AxilType.UNDEFINED;
				}
			}
		}
	}
	
	public void print() {
		String string = "";
		for(AxilConstant c : constants) {
			if(c.getType() == AxilType.BOOLEAN) string += "memory.setBoolean(" + c.getAddress() + ", " + c.getValue() + ");\n";
			if(c.getType() == AxilType.FLOAT) string += "memory.setFloat(" + c.getAddress() + ", " + c.getValue() + "f);\n";
			if(c.getType() == AxilType.INT) string += "memory.setInt(" + c.getAddress() + ", " + c.getValue() + ");\n";
			if(c.getType() == AxilType.STRING) string += "memory.setString(" + c.getAddress() + ", \"" + c.getValue() + "\");\n";
		}
		System.out.println(string);
	}

}
