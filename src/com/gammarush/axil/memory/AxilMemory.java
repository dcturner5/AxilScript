package com.gammarush.axil.memory;

import java.util.HashMap;

public class AxilMemory {
	
	private Object[] array;
	private AxilType[] typeArray;
	private int size = 0;
	
	private HashMap<String, AxilFunction> functionMap = new HashMap<String, AxilFunction>();
	
	public AxilMemory(int maxSize) {
		array = new Object[maxSize];
		typeArray = new AxilType[maxSize];
	}
	
	public int add(boolean value) {
		int address = size;
		array[address] = value;
		typeArray[address] = AxilType.BOOLEAN;
		size++;
		return address;
	}
	
	public int add(float value) {
		int address = size;
		array[address] = value;
		typeArray[address] = AxilType.FLOAT;
		size++;
		return address;
	}
	
	public int add(int value) {
		int address = size;
		array[address] = value;
		typeArray[address] = AxilType.INT;
		size++;
		return address;
	}
	
	public int add(String value) {
		int address = size;
		array[address] = value;
		typeArray[address] = AxilType.STRING;
		size++;
		return address;
	}
	
	public Object get(int address) {
		return array[address];
	}
	
	public AxilType getType(int address) {
		return typeArray[address];
	}
	
	public boolean getBoolean(int address) {
		if(typeArray[address] == AxilType.BOOLEAN) {
			return (boolean) array[address];
		}
		return toBoolean(get(address), getType(address));
	}
	
	public float getFloat(int address) {
		if(typeArray[address] == AxilType.FLOAT) {
			return (float) array[address];
		}
		return toFloat(get(address), getType(address));
	}
	
	public int getInt(int address) {
		if(typeArray[address] == AxilType.INT) {
			return (int) array[address];
		}
		return toInt(get(address), getType(address));
	}
	
	public String getString(int address) {
		if(typeArray[address] == AxilType.STRING) {
			return (String) array[address];
		}
		return toString(get(address), getType(address));
	}
	
	public AxilFunction getFunction(String name) {
		return functionMap.get(name);
	}
	
	public void setBoolean(int address, boolean value) {
		array[address] = value;
		typeArray[address] = AxilType.BOOLEAN;
	}
	
	public void setFloat(int address, float value) {
		array[address] = value;
		typeArray[address] = AxilType.FLOAT;
	}
	
	public void setInt(int address, int value) {
		array[address] = value;
		typeArray[address] = AxilType.INT;
	}
	
	public void setString(int address, String value) {
		array[address] = value;
		typeArray[address] = AxilType.STRING;
	}
	
	public void setFunction(AxilFunction function) {
		functionMap.put(function.getName(), function);
	}
	
	public boolean toBoolean(Object value, AxilType type) {
		switch(type) {
		case BOOLEAN:
			boolean booleanValue = (boolean) value;
			return booleanValue;
		case FLOAT:
			float floatValue = (float) value;
			return floatValue == 0 ? false : true;
		case INT:
			int intValue = (int) value;
			return intValue == 0 ? false : true;
		case STRING:
			String stringValue = (String) value;
			return stringValue.equals("true") ? true : false;
		default:
			return false;
		}
	}
	
	public float toFloat(Object value, AxilType type) {
		switch(type) {
		case BOOLEAN:
			boolean booleanValue = (boolean) value;
			return booleanValue == true ? 1.0f : 0.0f;
		case FLOAT:
			float floatValue = (float) value;
			return floatValue;
		case INT:
			int intValue = (int) value;
			return (float) intValue;
		case STRING:
			String stringValue = (String) value;
			try {
				return Float.valueOf(stringValue.trim()).floatValue();
			}
			catch (NumberFormatException e) {
				log("CANT CONVERT STRING \"" + stringValue + "\" TO FLOAT");
				return 0.0f;
			}
		default:
			return 0.0f;
		}
	}
	
	public int toInt(Object value, AxilType type) {
		switch(type) {
		case BOOLEAN:
			boolean booleanValue = (boolean) value;
			return booleanValue == true ? 1 : 0;
		case FLOAT:
			float floatValue = (float) value;
			return (int) floatValue;
		case INT:
			int intValue = (int) value;
			return intValue;
		case STRING:
			String stringValue = (String) value;
			try {
				return Integer.valueOf(stringValue.trim()).intValue();
			}
			catch (NumberFormatException e) {
				log("CANT CONVERT STRING \"" + stringValue + "\" TO INT");
				return 0;
			}
		default:
			return 0;
		}
	}
	
	public String toString(Object value, AxilType type) {
		return value.toString();
	}
	
	/*private String getTypeString(Type type) {
		switch(type) {
		case BOOLEAN:
			return "BOOLEAN";
		case FLOAT:
			return "FLOAT";
		case INT:
			return "INT";
		case STRING:
			return "STRING";
		default:
			return "UNDEFINED";
		}
	}*/
	
	private void log(String string) {
		String result = "\nAXILSCRIPT MEMORY ERROR: ";
		result += string;
		result += "\n";
		System.err.println(result);
	}

}
