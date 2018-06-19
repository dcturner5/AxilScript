package com.gammarush.axil.compiler;

import java.util.ArrayList;

import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.MethodHashMap;
import com.gammarush.axil.operators.AxilOperator;
import com.gammarush.axil.operators.ExpressionComponentList;
import com.gammarush.axil.operators.OperatorHashMap;

public class AxilCompiler {
	
	private static final String[] DATA_TYPES = new String[] {"boolean", "boolean[]", "float", "float[]", "int", "int[]", "string", "string[]"};
	//private static final String[] OPERATORS = new String[] {"+", "-", "*", "/", "<", ">", "!=", "==", "&&", "||"};
	private static final String[] ONE_SPACE_SYMBOLS = new String[] {"boolean", "double", "float", "int", "print", "return", "string"};
	private static final String[] NO_SPACE_SYMBOLS = new String[] {"**", "==", "!=", "!", "*", "/", "+", "-", "<", ">", "=", "(", ")", "[", "]", "{", "}", ",", "for", "if", "else", "while"};
	
	private static OperatorHashMap OPERATORS = new OperatorHashMap();
	
	private MethodHashMap methods;
	
	public AxilCompiler(MethodHashMap methods) {
		OPERATORS.put(new AxilOperator("**", "power", 15));
		OPERATORS.put(new AxilOperator("==", "equals", 10));
		OPERATORS.put(new AxilOperator("!=", "not_equals", 10));
		
		OPERATORS.put(new AxilOperator("!", "negate", 16, 1));
		OPERATORS.put(new AxilOperator("*", "multiply", 14));
		OPERATORS.put(new AxilOperator("/", "divide", 14));
		OPERATORS.put(new AxilOperator("+", "add", 13));
		OPERATORS.put(new AxilOperator("-", "subtract", 13));
		OPERATORS.put(new AxilOperator("<", "less_than", 11));
		OPERATORS.put(new AxilOperator(">", "greater_than", 11));
		OPERATORS.put(new AxilOperator("=", "assign", 3));
		
		this.methods = methods;
	}
	
	public int[] compile(String string, AxilCompilerMemory memory) {
		return compile(string, 0, memory);
	}
	
	public int[] compile(String string, int index, AxilCompilerMemory memory) {
		string = sanitize(string);
		
		int[] result = new int[] {};
		ArrayList<String> lines = getLines(string);
		for(String line : lines) {
			int[] instructions = compileLine(line, index + result.length, memory);
			result = combine(result, instructions);
		}
		
		return result;
	}
	
	private int[] compileExpression(String string, AxilCompilerMemory memory) {
		//System.out.println("%%% " + string + " %%%");
		
		ArrayList<int[]> preInstructions = new ArrayList<int[]>();
		int preSize = 0;
		int startIndex = 0;
		int operatorIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			
			if(bracketLayer == 0 && stringLayer == 0) {
				for(AxilOperator op : OPERATORS.getArray()) {
					if(op.equals(String.valueOf(c))) {
						operatorIndex = i;
					}
					else if(i != string.length() - 1 && op.equals(c + String.valueOf(string.charAt(i + 1)))) {
						operatorIndex = i + 1;
					}
				}
			}
			
			if(c == '(' && stringLayer == 0) {
				bracketLayer++;
				if(bracketLayer == 1) {
					startIndex = i;
				}
			}
			else if(c == ')' && stringLayer == 0) {
				bracketLayer--;
				if(bracketLayer == 0) {
					if(operatorIndex + 1 >= startIndex) {
						int[] instruction = compileExpression(string.substring(startIndex + 1, i), memory);
						int address = instruction[instruction.length - 1];
						string = string.substring(0, startIndex) + ":" + address + string.substring(i + 1);
						preInstructions.add(instruction);
						preSize += instruction.length;
					}
					else {
						//String methodName = string.substring(operatorIndex + 1, startIndex);
						if(operatorIndex != 0) {
							operatorIndex += 1;
						}
						
						int[] instruction = compileMethodCall(string.substring(operatorIndex, i), memory);
						int address = instruction[instruction.length - 1];
						string = string.substring(0, operatorIndex) + ":" + address + string.substring(i + 1);
						preInstructions.add(instruction);
						preSize += instruction.length;
					}
				}
			}
			if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
		}
		
		ExpressionComponentList componentList = new ExpressionComponentList(methods);
		startIndex = 0;
		for(int i = 0; i < string.length(); i++) {
			String c = String.valueOf(string.charAt(i));
			for(AxilOperator op : OPERATORS.getArray()) {
				boolean isOperator = false;
				int operatorLength = 0;
				
				if(op.equals(c)) {
					isOperator = true;
				}
				else if(i != string.length() - 1 && op.equals(c + String.valueOf(string.charAt(i + 1)))) {
					//System.out.println(op.getSymbol());
					isOperator = true;
					operatorLength = 1;
				}
				
				if(isOperator) {
					String variable = string.substring(startIndex, i);
					String operatorSymbol = string.substring(i, i + operatorLength + 1);
					
					if(!variable.equals("")) {
						componentList.add(variable);
					}
					if(!operatorSymbol.equals("")) {
						componentList.addOperator(OPERATORS.get(operatorSymbol));
						componentList.add(operatorSymbol);
					}
					
					startIndex = i + operatorLength + 1;
					i += operatorLength;
					break;
				}
			}
		}
		componentList.add(string.substring(startIndex));
		
		//componentList.print();
		
		int[] pre = new int[preSize];
		int[] main = componentList.compile(memory);
		
		//convert preInstructions (ArrayList<int[]>) to pre (int[])
		int index = 0;
		for(int i = 0; i < preInstructions.size(); i++) {
			int[] instruction = preInstructions.get(i);
			for(int j = 0; j < instruction.length; j++) {
				pre[index] = instruction[j];
				index++;
			}
		}
		
		int[] result = combine(pre, main);
		return result;
	}
	
	private int[] compileIfStatement(String string, int index, AxilCompilerMemory memory) {
		String condition = "";
		int startIndex = 0, endIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '(' && stringLayer == 0) {
				bracketLayer++;
				if(bracketLayer == 1) {
					startIndex = i + 1;
				}
			}
			else if(c == ')' && stringLayer == 0) {
				bracketLayer--;
				if(bracketLayer == 0) {
					endIndex = i;
					condition = string.substring(startIndex, endIndex);
					break;
				}
			}
			else if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
		}
		
		int[] conditionInstruction = compileExpression(condition, memory);
		int conditionResult = conditionInstruction.length == 0 ? memory.get(condition) : conditionInstruction[conditionInstruction.length - 1];
		
		String body = string.substring(endIndex + 2, string.length() - 1);
		int[] bodyInstruction = compile(body, index + conditionInstruction.length + 4, memory);
		
		int[] ifInstruction = new int[] {methods.getId("if"), conditionResult, index + conditionInstruction.length + 4, index + conditionInstruction.length + bodyInstruction.length + 4};
		
		int[] result = combine(combine(conditionInstruction, ifInstruction), bodyInstruction);
		return result;
	}
	
	private int[] compileLine(String string, int index, AxilCompilerMemory memory) {
		if(isIfStatement(string)) {
			//System.out.println("IF STATEMENT: " + string);
			return compileIfStatement(string, index, memory);
		}
		else if(isElseStatement(string)) {
			System.out.println("ELSE STATEMENT: " + string);
			return new int[] {};
		}
		else if(isWhileLoop(string)) {
			//System.out.println("WHILE LOOP: " + string);
			return compileWhileLoop(string, index, memory);
		}
		else if(isReturnStatement(string)) {
			System.out.println("RETURN STATEMENT: " + string);
			return new int[] {};
		}
		else {
			//System.out.println("EXPRESSION: " + string);
			return compileExpression(string, memory);
		}
	}
	
	private int[] compileMethodCall(String string, AxilCompilerMemory memory) {
		int pIndex = string.indexOf('(');
		String name = string.substring(0, pIndex);
		string = string.substring(pIndex + 1);
		
		ArrayList<int[]> args = new ArrayList<int[]>();
		ArrayList<Integer> argResults = new ArrayList<Integer>();
		int startIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '(' && stringLayer == 0) {
				bracketLayer++;
			}
			else if(c == ')' && stringLayer == 0) {
				bracketLayer--;
			}
			else if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
			else if(c == ',' && bracketLayer == 0 && stringLayer == 0) {
				int[] instruction = compileExpression(string.substring(startIndex, i), memory);
				if(instruction.length == 0) {
					args.add(new int[] {});
					argResults.add(memory.get(string.substring(startIndex, i)));
					startIndex = i + 1;
				}
				else {
					args.add(instruction);
					argResults.add(instruction[instruction.length - 1]);
					startIndex = i + 1;
				}
			}
		}
		
		AxilMethod method = methods.get(name);
		if(method == null) {
			System.err.println("METHOD " + name + " DOES NOT EXIST");
			return new int[] {};
		}
		
		if(method.getArgsLength() > 1) {
			int[] instruction = compileExpression(string.substring(startIndex), memory);
			if(instruction.length == 0) {
				args.add(new int[] {});
				argResults.add(memory.get(string.substring(startIndex)));
			}
			else {
				args.add(instruction);
				argResults.add(instruction[instruction.length - 1]);
			}
		}
		
		
		
		if(method.getArgsLength() - 1 != args.size()) {
			//throw error
			System.err.println("INVALID ARGUMENTS FOR METHOD " + name);
			System.err.println("EXPECTED " + (method.getArgsLength() - 1) + ", GOT " + args.size());
			return new int[] {};
		}
		//System.out.println("\n" + method.getId());
		
		int address = memory.reserve();
		int[] main = new int[method.getArgsLength() + 1];
		main[0] = method.getId();
		for(int i = 0; i < args.size(); i++) {
			int argResult = argResults.get(i);
			main[i + 1] = argResult;
		}
		main[main.length - 1] = address;
		
		int[] result = new int[] {};
		for(int i = 0; i < args.size(); i++) {
			int[] arg = args.get(i);
			result = combine(result, arg);
		}
		
		return combine(result, main);
	}
	
	private int[] compileWhileLoop(String string, int index, AxilCompilerMemory memory) {
		String condition = "";
		int startIndex = 0, endIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '(' && stringLayer == 0) {
				bracketLayer++;
				if(bracketLayer == 1) {
					startIndex = i + 1;
				}
			}
			else if(c == ')' && stringLayer == 0) {
				bracketLayer--;
				if(bracketLayer == 0) {
					endIndex = i;
					condition = string.substring(startIndex, endIndex);
					break;
				}
			}
			else if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
		}
		
		int[] conditionInstruction = compileExpression(condition, memory);
		int conditionResult = conditionInstruction.length == 0 ? memory.get(condition) : conditionInstruction[conditionInstruction.length - 1];
		
		String body = string.substring(endIndex + 2, string.length() - 1);
		int[] bodyInstruction = combine(compile(body, index + conditionInstruction.length + 4, memory), new int[] {methods.getId("goto"), index});
		
		int[] ifInstruction = new int[] {methods.getId("if"), conditionResult, index + conditionInstruction.length + 4, index + conditionInstruction.length + bodyInstruction.length + 4};
		
		int[] result = combine(combine(conditionInstruction, ifInstruction), bodyInstruction);
		return result;
	}
	
	private ArrayList<String> getLines(String string) {
		ArrayList<String> result = new ArrayList<String>();
		int startIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == ';' && bracketLayer == 0 && stringLayer == 0) {
				result.add(string.substring(startIndex, i));
				startIndex = i + 1;
			}
			
			if(c == '{') {
				bracketLayer++;
			}
			else if(c == '}') {
				bracketLayer--;
				if(bracketLayer == 0) {
					result.add(string.substring(startIndex, i + 1));
					startIndex = i + 1;
				}
			}
			
			if(c == '\"' && stringLayer == 0) {
				stringLayer++;
			}
			else if(c == '\"') {
				stringLayer--;
			}
		}
		return result;
	}
	
	private boolean isElseStatement(String string) {
		return string.indexOf("else") == 0;
	}
	
	private boolean isIfStatement(String string) {
		return string.indexOf("if") == 0;
	}
	
	private boolean isReturnStatement(String string) {
		return string.indexOf("return") == 0;
	}
	
	private boolean isWhileLoop(String string) {
		return string.indexOf("while") == 0;
	}
	
	private String sanitize(String string) {
		String temp = string;
		
		string = string.replace("\t", "");
		
		for(String s : ONE_SPACE_SYMBOLS) {
			string = string.replace(" " + s, s);
			string = string.replace(s + "  ", s + " ");
		}
		
		for(String s : NO_SPACE_SYMBOLS) {
			string = string.replace(" " + s, s);
			string = string.replace(s + " ", s);
		}
		
		return string.equals(temp) ? string : sanitize(string);
	}
	
	//move to util class one day
	public static int[] combine(int[] a, int[] b){
        int length = a.length + b.length;
        int[] result = new int[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
