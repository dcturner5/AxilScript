package com.gammarush.axil.compiler.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.AxilMethodMap;

public class AxilOperatorCompiler {
	
	private class OperatorInstance {
		private AxilOperator axilOperator;
		private int index;
			
		public OperatorInstance(AxilOperator axilOperator, int index) {
			this.axilOperator = axilOperator;
			this.index = index;
		}
			
		public AxilOperator getOperator() {
			return axilOperator;
		}
			
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}
	
	private ArrayList<String> components = new ArrayList<String>();
	private ArrayList<OperatorInstance> operators = new ArrayList<OperatorInstance>();
	
	private AxilMethodMap methods;
	
	public AxilOperatorCompiler(AxilMethodMap methods) {
		this.methods = methods;
	}
	
	public void add(String string) {
		components.add(string);
	}
	
	public void addOperator(AxilOperator operator) {
		operators.add(new OperatorInstance(operator, components.size()));
	}
	
	private void sort() {
		Collections.sort(operators, new Comparator<OperatorInstance>() {
			@Override
			public int compare(OperatorInstance a, OperatorInstance b) {
				if(a.getOperator().getPriority() > b.getOperator().getPriority()) return -1;
				if(a.getOperator().getPriority() < b.getOperator().getPriority()) return 1;
				return 0;
			}
		});
	}
	
	public int[] compile(AxilCompilerMemory memory) {
		int result[];
		int size = 0;
		int[][] instructions = new int[operators.size()][];
		
		sort();
		for(int i = 0; i < operators.size(); i++) {
			instructions[i] = compileOperation(operators.get(i), memory);
			size += instructions[i].length;
		}
		
		result = new int[size];
		
		int index = 0;
		for(int i = 0; i < operators.size(); i++) {
			for(int j = 0; j < instructions[i].length; j++) {
				result[index] = instructions[i][j];
				index++;
			}
		}
		
		return result;
	}
	
	public int[] compileOperation(OperatorInstance operatorInstance, AxilCompilerMemory memory) {
		AxilOperator operator = operatorInstance.getOperator();
		AxilMethod method = methods.get(operator.getMethodName());
		int[] result = new int[method.getArgsLength() + 1];
		
		result[0] = method.getId();
		
		int address = memory.reserve();
		
		if(operator.getArgsLength() == 1) {
			String component0 = components.get(operatorInstance.getIndex() + 1);
			result[1] = memory.get(component0);
			components.set(operatorInstance.getIndex(), ":" + address);
			components.remove(component0);
		}
		else if(operator.getArgsLength() == 2) {
			String component0 = components.get(operatorInstance.getIndex() - 1);
			String component1 = components.get(operatorInstance.getIndex());
			String component2 = components.get(operatorInstance.getIndex() + 1);
			result[1] = memory.get(component0);
			result[2] = memory.get(component2);
			components.set(operatorInstance.getIndex() - 1, ":" + address);
			components.remove(component1);
			components.remove(component2);
		}
		
		result[result.length - 1] = address;
		
		for(OperatorInstance e : operators) {
			if(e.getIndex() > operatorInstance.getIndex()) {
				e.setIndex(e.getIndex() - operator.getArgsLength());
			}
		}
		
		return result;
	}
	
	public void print() {
		System.out.println("\n***");
		for(OperatorInstance op : operators) {
			System.out.println(op.getOperator().getSymbol() + "[" + op.getIndex() + "]");
		}
		for(String c : components) {
			System.out.print(c + " ");
		}
		System.out.println("\n***\n");
	}

}
