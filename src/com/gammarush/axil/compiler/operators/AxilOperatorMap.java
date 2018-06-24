package com.gammarush.axil.compiler.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AxilOperatorMap {
	
	private HashMap<String, AxilOperator> nameMap = new HashMap<String, AxilOperator>();
	public ArrayList<AxilOperator> array = new ArrayList<AxilOperator>();
	
	public AxilOperator get(String name) {
		return nameMap.get(name);
	}
	
	public ArrayList<AxilOperator> getArray() {
		return array;
	}
	
	public void put(AxilOperator e) {
		nameMap.put(e.getSymbol(), e);
		array.add(e);
		sort();
	}
	
	private void sort() {
		Collections.sort(array, new Comparator<AxilOperator>() {
			@Override
			public int compare(AxilOperator a, AxilOperator b) {
				if(a.getSymbol().length() > b.getSymbol().length()) return -1;
				if(a.getSymbol().length() < b.getSymbol().length()) return 1;
				return 0;
			}
		});
	}

}
