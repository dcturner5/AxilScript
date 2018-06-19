package com.gammarush.axil.compiler.memory;

import java.util.ArrayList;
import java.util.HashMap;

import com.gammarush.axil.memory.AxilMemory;

public class AxilCompilerMemory {
	
	private class Constant {
		
		private int address;
		private Object value;
		private Type type;
		
		public Constant(int address, Object value, Type type) {
			this.address = address;
			this.value = value;
			this.type = type;
		}
	}
	
	public enum Type {
		BOOLEAN, FLOAT, INT, STRING, UNDEFINED
	}
	
	private HashMap<String, Integer> addressMap = new HashMap<String, Integer>();
	private ArrayList<Constant> constants = new ArrayList<Constant>();
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
			Type type = parseType(name);
			if(type == Type.BOOLEAN) {
				int address = set(name, parseBoolean(name));
				return address;
			}
			else if(type == Type.FLOAT) {
				int address = set(name, parseFloat(name));
				return address;
			}
			else if(type == Type.INT) {
				int address = set(name, parseInt(name));
				return address;
			}
			else if(type == Type.STRING) {
				int address = set(name, parseString(name));
				return address;
			}
			else {
				int address = set(name);
				return address;
			}
		}
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
		constants.add(new Constant(address, value, Type.BOOLEAN));
		return address;
	}
	
	public int set(String name, float value) {
		int address = set(name);
		constants.add(new Constant(address, value, Type.FLOAT));
		return address;
	}
	
	public int set(String name, int value) {
		int address = set(name);
		constants.add(new Constant(address, value, Type.INT));
		return address;
	}
	
	public int set(String name, String value) {
		int address = set(name);
		constants.add(new Constant(address, value, Type.STRING));
		return address;
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
	
	private Type parseType(String string) {
		char first = string.charAt(0);
		char last = string.charAt(string.length() - 1);
		if(first == '\"' && last == '\"') {
			return Type.STRING;
		}
		else if(string.equals("true") || string.equals("false")) {
			return Type.BOOLEAN;
		}
		else {
			if(string.indexOf('.') != -1) {
				try {
					Float.valueOf(string.trim()).floatValue();
					return Type.FLOAT;
				}
				catch (NumberFormatException e) {
					return Type.UNDEFINED;
				}
			}
			else {
				try {
					Integer.valueOf(string.trim()).intValue();
					return Type.INT;
				}
				catch (NumberFormatException e) {
					return Type.UNDEFINED;
				}
			}
		}
	}
	
	public void print() {
		String string = "";
		for(Constant c : constants) {
			if(c.type == Type.BOOLEAN) string += "memory.setBoolean(" + c.address + ", " + c.value + ");\n";
			if(c.type == Type.FLOAT) string += "memory.setFloat(" + c.address + ", " + c.value + "f);\n";
			if(c.type == Type.INT) string += "memory.setInt(" + c.address + ", " + c.value + ");\n";
			if(c.type == Type.STRING) string += "memory.setString(\"" + c.address + ", " + c.value + "\");\n";
		}
		System.out.println(string);
	}
	
	public void load(AxilMemory memory) {
		for(Constant c : constants) {
			if(c.type == Type.BOOLEAN) memory.setBoolean(c.address, (boolean) c.value);
			if(c.type == Type.FLOAT) memory.setFloat(c.address, (float) c.value);
			if(c.type == Type.INT) memory.setInt(c.address, (int) c.value);
			if(c.type == Type.STRING) memory.setString(c.address, (String) c.value);
		}
	}

}
