package com.gammarush.axil.methods;

import java.util.ArrayList;
import java.util.HashMap;

import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.memory.AxilMemory.Type;

public class MethodHashMap {
	
	private HashMap<Integer, AxilMethod> idMap = new HashMap<Integer, AxilMethod>();
	private HashMap<String, AxilMethod> nameMap = new HashMap<String, AxilMethod>();
	private ArrayList<AxilMethod> array = new ArrayList<AxilMethod>();
	
	public MethodHashMap() {
		put("assign", 3, (int[] args, AxilMemory memory) -> {
			int address = args[0];
			Type type = memory.getType(args[1]);
			if(type == Type.BOOLEAN) {
				boolean value = memory.getBoolean(args[1]);
				memory.setBoolean(address, value);
			}
			if(type == Type.FLOAT) {
				float value = memory.getFloat(args[1]);
				memory.setFloat(address, value);
			}
			if(type == Type.INT) {
				int value = memory.getInt(args[1]);
				memory.setInt(address, value);
			}
			if(type == Type.STRING) {
				String value = memory.getString(args[1]);
				memory.setString(address, value);
			}
			return -1;
		});
		put("add", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				String value = memory.getString(args[0]) + memory.getString(args[1]);
				memory.setString(address, value);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = memory.getFloat(args[0]) + memory.getFloat(args[1]);
				memory.setFloat(address, value);
			}
			else {
				int value = memory.getInt(args[0]) + memory.getInt(args[1]);
				memory.setInt(address, value);
			}
			return -1;
		});
		put("subtract", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setInt(address, 0);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = memory.getFloat(args[0]) - memory.getFloat(args[1]);
				memory.setFloat(address, value);
			}
			else {
				int value = memory.getInt(args[0]) - memory.getInt(args[1]);
				memory.setInt(address, value);
			}
			return -1;
		});
		put("multiply", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setInt(address, 0);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = memory.getFloat(args[0]) * memory.getFloat(args[1]);
				memory.setFloat(address, value);
			}
			else {
				int value = memory.getInt(args[0]) * memory.getInt(args[1]);
				memory.setInt(address, value);
			}
			return -1;
		});
		put("divide", 3, (int[] args, AxilMemory memory) -> {
			//check for divide by zero
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setInt(address, 0);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = memory.getFloat(args[0]) / memory.getFloat(args[1]);
				memory.setFloat(address, value);
			}
			else {
				int value = memory.getInt(args[0]) / memory.getInt(args[1]);
				memory.setInt(address, value);
			}
			return -1;
		});
		put("power", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setInt(address, 0);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = (float) Math.pow(memory.getFloat(args[0]), memory.getFloat(args[1]));
				memory.setFloat(address, value);
			}
			else {
				int value = (int) Math.pow(memory.getInt(args[0]), memory.getInt(args[1]));
				memory.setInt(address, value);
			}
			return -1;
		});
		put("equals", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				boolean value = memory.getString(args[0]).equals(memory.getString(args[1]));
				memory.setBoolean(address, value);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) == memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else if (type0 == Type.INT || type0 == Type.INT){
				boolean value = memory.getInt(args[0]) == memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getBoolean(args[0]) == memory.getBoolean(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("not_equals", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				boolean value = memory.getString(args[0]).equals(memory.getString(args[1]));
				memory.setBoolean(address, value);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) != memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else if (type0 == Type.INT || type0 == Type.INT){
				boolean value = memory.getInt(args[0]) != memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getBoolean(args[0]) != memory.getBoolean(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("less_than", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setBoolean(address, false);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) < memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getInt(args[0]) < memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("greater_than", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setBoolean(address, false);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) > memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getInt(args[0]) > memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("less_than_or_equals", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setBoolean(address, false);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) <= memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getInt(args[0]) <= memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("greater_than_or_equals", 3, (int[] args, AxilMemory memory) -> {
			int address = args[2];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setBoolean(address, false);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				boolean value = memory.getFloat(args[0]) >= memory.getFloat(args[1]);
				memory.setBoolean(address, value);
			}
			else {
				boolean value = memory.getInt(args[0]) >= memory.getInt(args[1]);
				memory.setBoolean(address, value);
			}
			return -1;
		});
		put("square_root", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			Type type0 = memory.getType(args[0]);
			Type type1 = memory.getType(args[1]);
			if(type0 == Type.STRING || type1 == Type.STRING) {
				//throw error
				memory.setInt(address, 0);
			}
			else if(type0 == Type.FLOAT || type1 == Type.FLOAT) {
				float value = (float) Math.sqrt(memory.getFloat(args[0]));
				memory.setFloat(address, value);
			}
			else {
				int value = (int) Math.sqrt(memory.getInt(args[0]));
				memory.setInt(address, value);
			}
			return -1;
		});
		put("if", 3, (int[] args, AxilMemory memory) -> {
			boolean condition = memory.getBoolean(args[0]);
			if(condition) return args[1];
			return args[2];
		});
		put("goto", 1, (int[] args, AxilMemory memory) -> {
			return args[0];
		});
		put("goto_from_memory", 1, (int[] args, AxilMemory memory) -> {
			return memory.getInt(args[0]);
		});
		put("print", 2, (int[] args, AxilMemory memory) -> {
			System.out.println(memory.get(args[0]));
			return -1;
		});
		put("negate", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			boolean value = !memory.getBoolean(args[0]);
			memory.setBoolean(address, value);
			return -1;
		});
		put("random", 1, (int[] args, AxilMemory memory) -> {
			int address = args[0];
			memory.setFloat(address, (float) Math.random());
			return -1;
		});
		put("boolean", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			memory.setBoolean(address, memory.getBoolean(args[0]));
			return -1;
		});
		put("float", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			memory.setFloat(address, memory.getFloat(args[0]));
			return -1;
		});
		put("int", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			memory.setInt(address, memory.getInt(args[0]));
			return -1;
		});
		put("string", 2, (int[] args, AxilMemory memory) -> {
			int address = args[1];
			memory.setString(address, memory.getString(args[0]));
			return -1;
		});
		/*put("assign_array", -1, (int[] args, AxilMemory memory) -> {
			int address = (int) args[args.length - 2];
			Object[] value = new Object[args.length - 2];
			for(int i = 0; i < value.length; i++) {
				value[i] = storage[args[i]];
			}
			storage[address] = value;
			return -1;
		});
		put("get_array", 3, (int[] args, AxilMemory memory) -> {
			int address = (int) args[2];
			Object[] array = (Object[]) storage[args[0]];
			Object value = array[(int) storage[args[1]]];
			storage[address] = value;
			return -1;
		});*/
	}
	
	public int execute(int id, int[] args, AxilMemory memory) {
		return get(id).execute(args, memory);
	}
	
	public int execute(String name, int[] args, AxilMemory memory) {
		return get(name).execute(args, memory);
	}
	
	public AxilMethod get(int id) {
		return idMap.get(id);
	}
	
	public AxilMethod get(String name) {
		return nameMap.get(name);
	}
	
	public int getId(String name) {
		AxilMethod e = nameMap.get(name);
		if(e == null) return -1;
		return e.getId();
	}
	
	public void put(String name, int argsLength, AxilMethodInterface method) {
		AxilMethod e = new AxilMethod(array.size(), name, argsLength, method);
		idMap.put(e.getId(), e);
		nameMap.put(e.getName(), e);
		array.add(e);
	}

}
