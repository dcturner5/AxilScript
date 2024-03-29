package com.gammarush.axil.compiler;

import java.util.ArrayList;

import com.gammarush.axil.AxilLoader;
import com.gammarush.axil.AxilScript;
import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.compiler.operators.AxilOperator;
import com.gammarush.axil.compiler.operators.AxilOperatorCompiler;
import com.gammarush.axil.compiler.operators.AxilOperatorMap;
import com.gammarush.axil.memory.AxilFunction;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.AxilMethodMap;

public class AxilCompiler {
	
	private static AxilOperatorMap OPERATORS = new AxilOperatorMap();
	
	private AxilMethodMap methods;
	
	public AxilCompiler(AxilMethodMap methods) {
		this.methods = methods;
		
		OPERATORS.put(new AxilOperator("!", "negate", 16, 1));
		OPERATORS.put(new AxilOperator("++", "increment", 16, 1));
		OPERATORS.put(new AxilOperator("--", "decrement", 16, 1));
		OPERATORS.put(new AxilOperator("**", "power", 15));
		OPERATORS.put(new AxilOperator("*", "multiply", 14));
		OPERATORS.put(new AxilOperator("/", "divide", 14));
		OPERATORS.put(new AxilOperator("%", "remainder", 14));
		OPERATORS.put(new AxilOperator("+", "add", 13));
		OPERATORS.put(new AxilOperator("-", "subtract", 13));
		OPERATORS.put(new AxilOperator("<", "less_than", 11));
		OPERATORS.put(new AxilOperator(">", "greater_than", 11));
		OPERATORS.put(new AxilOperator("<=", "less_than_or_equals", 11));
		OPERATORS.put(new AxilOperator(">=", "greater_than_or_equals", 11));
		OPERATORS.put(new AxilOperator("==", "equals", 10));
		OPERATORS.put(new AxilOperator("!=", "not_equals", 10));
		OPERATORS.put(new AxilOperator("===", "equals_strict", 10));
		OPERATORS.put(new AxilOperator("!==", "not_equals_strict", 10));
		OPERATORS.put(new AxilOperator("&&", "and", 6));
		OPERATORS.put(new AxilOperator("||", "or", 5));
		OPERATORS.put(new AxilOperator("=", "assign", 3));
		OPERATORS.put(new AxilOperator("+=", "assign_add", 3));
		OPERATORS.put(new AxilOperator("-=", "assign_subtract", 3));
		OPERATORS.put(new AxilOperator("*=", "assign_multiply", 3));
		OPERATORS.put(new AxilOperator("/=", "assign_divide", 3));
		OPERATORS.put(new AxilOperator("%=", "assign_remainder", 3));
		OPERATORS.put(new AxilOperator("**=", "assign_power", 3));
	}
	
	public void compileFile(String path) {
		String string = sanitize(AxilLoader.loadTextFile(path));
		AxilCompilerMemory memory = new AxilCompilerMemory();
		int[] instructions = compile(string, 0, memory);
		AxilLoader.save(path, instructions, memory);
	}
	
	public int[] compileFile(String path, int index, AxilCompilerMemory memory) {
		String string = sanitize(AxilLoader.loadTextFile(path));
		int[] instructions = compile(string, index, memory);
		return instructions;
	}
	
	public AxilScript compileString(String string) {
		string = sanitize(string);
		AxilCompilerMemory memory = new AxilCompilerMemory();
		int[] instructions = compile(string, 0, memory);
		return new AxilScript(instructions, new AxilMemory(1024, memory), methods);
	}
	
	private int[] compile(String string, int index, AxilCompilerMemory memory) {
		int[] result = new int[] {};
		ArrayList<String> lines = getLines(string);
		
		int count = 0;
		ArrayList<String> removeQueue = new ArrayList<String>();
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(count > 0 && (isElseIfStatement(line) || isElseStatement(line))) {
				lines.set(i - count, lines.get(i - count) + line);
				count = (isIfStatement(line) || isElseIfStatement(line)) ? count + 1 : 0;
				removeQueue.add(line);
			}
			else {
				count = (isIfStatement(line) || isElseIfStatement(line)) ? count + 1 : 0;
			}
		}
		
		for(String line : removeQueue) {
			lines.remove(line);
		}
		
		for(String line : lines) {
			int[] instructions = compileLine(line, index + result.length, memory);
			result = combine(result, instructions);
		}
		
		return result;
	}
	
	private int[] compileLine(String string, int index, AxilCompilerMemory memory) {
		if(isFunctionDeclaration(string)) {
			return compileFunctionDeclaration(string, index, memory);
		}
		else if(isIfStatement(string)) {
			//System.out.println("IF STATEMENT: " + string);
			return compileIfStatement(string, index, memory);
		}
		else if(isImportStatement(string)) {
			return compileImportStatement(string, index, memory);
		}
		else if(isReturnStatement(string)) {
			System.out.println("RETURN STATEMENT: " + string);
			return new int[] {};
		}
		else if(isWhileLoop(string)) {
			//System.out.println("WHILE LOOP: " + string);
			return compileWhileLoop(string, index, memory);
		}
		else {
			//System.out.println("EXPRESSION: " + string);
			return compileExpression(string, index, memory);
		}
	}
	
	private int[] compileExpression(String string, int index, AxilCompilerMemory memory) {
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
						int[] instruction = compileExpression(string.substring(startIndex + 1, i), index, memory);
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
						
						int[] instruction = compileMethodCall(string.substring(operatorIndex, i + 1), index, memory);
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
		
		AxilOperatorCompiler operatorCompiler = new AxilOperatorCompiler(methods);
		startIndex = 0;
		stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			String c = string.substring(i, i + 1);
			if(c.equals("\"")) {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
			if(stringLayer == 0) {
			for(AxilOperator op : OPERATORS.getArray()) {
				boolean isOperator = false;
				int operatorLength = 1;
				
				if(op.equals(c)) {
					isOperator = true;
				}
				else if(i + op.getSymbol().length() < string.length() && op.equals(string.substring(i, i + op.getSymbol().length()))) {
					isOperator = true;
					operatorLength = op.getSymbol().length();
				}
				
				if(isOperator) {
					String variable = string.substring(startIndex, i);
					String operatorSymbol = string.substring(i, i + operatorLength);
					
					if(!variable.equals("")) {
						operatorCompiler.add(variable);
					}
					if(!operatorSymbol.equals("")) {
						operatorCompiler.addOperator(OPERATORS.get(operatorSymbol));
						operatorCompiler.add(operatorSymbol);
					}
					
					startIndex = i + operatorLength;
					i += operatorLength - 1;
					break;
				}
			}
		}
		}
		operatorCompiler.add(string.substring(startIndex));
		
		int[] pre = new int[preSize];
		int[] main = operatorCompiler.compile(memory);
		
		//convert preInstructions (ArrayList<int[]>) to pre (int[])
		int pIndex = 0;
		for(int i = 0; i < preInstructions.size(); i++) {
			int[] instruction = preInstructions.get(i);
			for(int j = 0; j < instruction.length; j++) {
				pre[pIndex] = instruction[j];
				pIndex++;
			}
		}
		
		int[] result = combine(pre, main);
		return result;
	}
	
	private int[] compileFunctionDeclaration(String string, int index, AxilCompilerMemory memory) {
		int pIndex = string.indexOf('(');
		String name = string.substring("function".length(), pIndex);
		string = string.substring(pIndex + 1);
		
		ArrayList<Integer> args = new ArrayList<Integer>();
		int startIndex = 0;
		int bracketLayer = 0, stringLayer = 0;
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c == '(' && stringLayer == 0) {
				bracketLayer++;
			}
			else if(c == ')' && stringLayer == 0) {
				bracketLayer--;
				if(bracketLayer == -1) {
					String arg = string.substring(startIndex, i);
					if(!arg.equals("")) {
						args.add(memory.get(arg));
					}
					startIndex = i + 1;
					break;
				}
			}
			else if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
			else if(c == ',' && bracketLayer == 0 && stringLayer == 0) {
				args.add(memory.get(string.substring(startIndex, i)));
				startIndex = i + 1;
			}
		}
		
		//this address is where to post goto goes after function is finished, allows for returns?
		int returnAddress = memory.reserve();
		int[] postInstruction = new int[] {methods.getId("goto_memory"), returnAddress};
		
		String body = string.substring(startIndex + 1, string.length() - 1);
		int[] bodyInstruction = combine(compile(body, index + 2, memory), postInstruction);
		
		int address = index + 2;
		int[] preInstruction = new int[] {methods.getId("goto"), address + bodyInstruction.length};
		
		int[] argAddresses = new int[args.size()];
		for(int i = 0; i < args.size(); i++) {
			argAddresses[i] = args.get(i);
		}
		
		memory.setFunction(new AxilFunction(name, address, returnAddress, argAddresses));
		
		return combine(preInstruction, bodyInstruction);
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
		
		int[] conditionInstruction = compileExpression(condition, index, memory);
		int conditionResult = conditionInstruction.length == 0 ? memory.get(condition) : conditionInstruction[conditionInstruction.length - 1];
		
		String body = string.substring(endIndex + 2, string.length() - 1);
		int[] bodyInstruction = compile(body, index + conditionInstruction.length + 4, memory);
		
		int[] ifInstruction = new int[] {methods.getId("if"), conditionResult, index + conditionInstruction.length + 4, index + conditionInstruction.length + bodyInstruction.length + 4};
		
		int[] result = combine(combine(conditionInstruction, ifInstruction), bodyInstruction);
		return result;
	}
	
	private int[] compileImportStatement(String string, int index, AxilCompilerMemory memory) {
		string = string.substring("import".length());
		return compileFile(string, index, memory);
	}
	
	private int[] compileMethodCall(String string, int index, AxilCompilerMemory memory) {
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
				if(bracketLayer == -1) {
					String expression = string.substring(startIndex, i);
					if(expression.equals("")) break;
					
					int[] instruction = compileExpression(expression, index, memory);
					if(instruction.length == 0) {
						args.add(new int[] {});
						argResults.add(memory.get(expression));
					}
					else {
						args.add(instruction);
						argResults.add(instruction[instruction.length - 1]);
					}
					break;
				}
			}
			else if(c == '\"') {
				if(stringLayer == 0) stringLayer = 1;
				else stringLayer = 0;
			}
			else if(c == ',' && bracketLayer == 0 && stringLayer == 0) {
				int[] instruction = compileExpression(string.substring(startIndex, i), index, memory);
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
		
		AxilFunction function = memory.getFunction(name);
		if(function != null) {
			int[] result = new int[] {};
			int[] argAddresses = function.getArgAddresses();
			for(int i = 0; i < args.size(); i++) {
				int[] arg = args.get(i);
				if(arg.length > 0) {
					arg[arg.length - 1] = argAddresses[i];
					result = combine(result, arg);
				}
				else {
					arg = new int[] {methods.getId("assign"), argAddresses[i], argResults.get(i), -1};
					result = combine(result, arg);
				}
			}
			
			int[] main = new int[] {methods.getId("assign_runtime"), function.getReturnAddress(), index + result.length + 6, -1, methods.getId("goto"), function.getAddress()};
			return combine(result, main);
		}
		else {
			AxilMethod method = methods.get(name);
			if(method == null) {
				System.err.println("METHOD " + name + " DOES NOT EXIST");
				return new int[] {};
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
		
		int[] conditionInstruction = compileExpression(condition, index, memory);
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
		return string.indexOf("else") == 0 && string.indexOf("elseif") != 0;
	}
	
	private boolean isElseIfStatement(String string) {
		return string.indexOf("elseif") == 0;
	}
	
	private boolean isFunctionDeclaration(String string) {
		return string.indexOf("function") == 0;
	}
	
	private boolean isIfStatement(String string) {
		return string.indexOf("if") == 0;
	}
	
	private boolean isImportStatement(String string) {
		return string.indexOf("import") == 0;
	}
	
	private boolean isReturnStatement(String string) {
		return string.indexOf("return") == 0;
	}
	
	private boolean isWhileLoop(String string) {
		return string.indexOf("while") == 0;
	}
	
	private String sanitize(String string) {
		return string.replaceAll("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)", "");
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
