package com.gammarush.axil.memory;

public class AxilMemory {
	
	private Object[] array;
	private Type[] typeArray;
	private int size = 0;
	
	public enum Type {
		BOOLEAN, FLOAT, INT, STRING, UNDEFINED
	}
	
	public AxilMemory(int maxSize) {
		array = new Object[maxSize];
		typeArray = new Type[maxSize];
	}
	
	public int add(boolean value) {
		int address = size;
		array[address] = value;
		typeArray[address] = Type.BOOLEAN;
		size++;
		return address;
	}
	
	public int add(float value) {
		int address = size;
		array[address] = value;
		typeArray[address] = Type.FLOAT;
		size++;
		return address;
	}
	
	public int add(int value) {
		int address = size;
		array[address] = value;
		typeArray[address] = Type.INT;
		size++;
		return address;
	}
	
	public int add(String value) {
		int address = size;
		array[address] = value;
		typeArray[address] = Type.STRING;
		size++;
		return address;
	}
	
	public Object get(int address) {
		return array[address];
	}
	
	public Type getType(int address) {
		return typeArray[address];
	}
	
	/*public boolean getBoolean(int address) {
		if(typeArray[address] == Type.BOOLEAN) {
			return (boolean) array[address];
		}
		return false;
	}*/
	
	public boolean getBoolean(int address) {
		if(typeArray[address] == Type.BOOLEAN) {
			return (boolean) array[address];
		}
		return toBoolean(get(address), getType(address));
	}
	
	/*public float getFloat(int address) {
		if(typeArray[address] == Type.FLOAT) {
			return (float) array[address];
		}
		return 0;
	}*/
	
	public float getFloat(int address) {
		if(typeArray[address] == Type.FLOAT) {
			return (float) array[address];
		}
		return toFloat(get(address), getType(address));
	}
	
	/*public int getInt(int address) {
		if(typeArray[address] == Type.INT) {
			return (int) array[address];
		}
		return 0;
	}*/
	
	public int getInt(int address) {
		if(typeArray[address] == Type.INT) {
			return (int) array[address];
		}
		return toInt(get(address), getType(address));
	}
	
	/*public String getString(int address) {
		if(typeArray[address] == Type.STRING) {
			return (String) array[address];
		}
		return "";
	}*/
	
	public String getString(int address) {
		if(typeArray[address] == Type.STRING) {
			return (String) array[address];
		}
		return toString(get(address), getType(address));
	}
	
	public void setBoolean(int address, boolean value) {
		array[address] = value;
		typeArray[address] = Type.BOOLEAN;
	}
	
	public void setFloat(int address, float value) {
		array[address] = value;
		typeArray[address] = Type.FLOAT;
	}
	
	public void setInt(int address, int value) {
		array[address] = value;
		typeArray[address] = Type.INT;
	}
	
	public void setString(int address, String value) {
		array[address] = value;
		typeArray[address] = Type.STRING;
	}
	
	public boolean toBoolean(Object value, Type type) {
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
	
	public float toFloat(Object value, Type type) {
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
				log("CANT CONVERT STRING TO FLOAT");
				e.printStackTrace();
				return 0.0f;
			}
		default:
			return 0.0f;
		}
	}
	
	public int toInt(Object value, Type type) {
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
				log("CANT CONVERT STRING TO INT");
				e.printStackTrace();
				return 0;
			}
		default:
			return 0;
		}
	}
	
	public String toString(Object value, Type type) {
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
		String result = "\n\nAXILSCRIPT MEMORY ERROR:\n";
		result += string;
		result += "\n";
		System.err.println(result);
	}

}
