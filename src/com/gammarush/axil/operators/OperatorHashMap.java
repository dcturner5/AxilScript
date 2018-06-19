package com.gammarush.axil.operators;

import java.util.ArrayList;
import java.util.HashMap;

public class OperatorHashMap {
	
	private HashMap<String, AxilOperator> nameMap = new HashMap<String, AxilOperator>();
	private ArrayList<AxilOperator> array = new ArrayList<AxilOperator>();
	
	public AxilOperator get(String name) {
		return nameMap.get(name);
	}
	
	public ArrayList<AxilOperator> getArray() {
		return array;
	}
	
	public void put(AxilOperator e) {
		nameMap.put(e.getSymbol(), e);
		array.add(e);
	}

}
