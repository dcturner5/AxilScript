package com.gammarush.axil.methods;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodHashMap {
	
	private HashMap<Integer, AxilMethod> idMap = new HashMap<Integer, AxilMethod>();
	private HashMap<String, AxilMethod> nameMap = new HashMap<String, AxilMethod>();
	private ArrayList<AxilMethod> array = new ArrayList<AxilMethod>();
	
	public MethodHashMap() {
		put("assign", 2, (int[] args, Object[] storage) -> {
			int address = (int) args[1];
			Object value = storage[args[0]];
			storage[address] = value;
			return -1;
		});
		put("add", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			int value = (int) storage[args[0]] + (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("sub", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			int value = (int) storage[args[0]] - (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("mult", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			int value = (int) storage[args[0]] * (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("div", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			int value = (int) storage[args[0]] / (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("equal", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			boolean value = (int) storage[args[0]] == (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("less", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			boolean value = (int) storage[args[0]] < (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("greater", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			boolean value = (int) storage[args[0]] > (int) storage[args[1]];
			storage[address] = value;
			return -1;
		});
		put("if", 3, (int[] args, Object[] storage) -> {
			boolean condition = (boolean) storage[args[0]];
			if(condition) return args[1];
			return args[2];
		});
		put("goto", 1, (int[] args, Object[] storage) -> {
			return args[0];
		});
		put("print", 1, (int[] args, Object[] storage) -> {
			System.out.println(storage[args[0]]);
			return -1;
		});
		put("assign_array", -1, (int[] args, Object[] storage) -> {
			int address = (int) args[args.length - 2];
			Object[] value = new Object[args.length - 2];
			for(int i = 0; i < value.length; i++) {
				value[i] = storage[args[i]];
			}
			storage[address] = value;
			return -1;
		});
		put("get_array", 3, (int[] args, Object[] storage) -> {
			int address = (int) args[2];
			Object[] array = (Object[]) storage[args[0]];
			Object value = array[(int) storage[args[1]]];
			storage[address] = value;
			return -1;
		});
	}
	
	public int execute(int id, int[] args, Object[] storage) {
		return get(id).execute(args, storage);
	}
	
	public int execute(String name, int[] args, Object[] storage) {
		return get(name).execute(args, storage);
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
