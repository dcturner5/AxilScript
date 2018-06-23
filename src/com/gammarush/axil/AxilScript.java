package com.gammarush.axil;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JFrame;

import com.gammarush.axil.compiler.AxilCompiler;
import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.memory.AxilFunction;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.AxilMethodMap;

public class AxilScript {
	
	private String path;
	private int[] instructions;
	
	private AxilCompiler compiler;
	private AxilMemory memory;
	private AxilMethodMap methods;
	
	public AxilScript(String path) {
		this.path = path;
		
		memory = new AxilMemory(1024);
		methods = new AxilMethodMap();
		
		compiler = new AxilCompiler(methods);
	}
	
	public void compile() {
		String string = AxilLoader.load(path);
		
		AxilCompilerMemory compilerMemory = new AxilCompilerMemory();
		instructions = compiler.compile(string, compilerMemory);
		memory.load(compilerMemory);
		
		System.out.println("*****");
		String s = path + ": ";
		for(int i : instructions) {
			s += i + ", ";
		}
		System.out.println(s.substring(0, s.length() - 2));
		System.out.println("*****");
	}
	
	public void run() {
		for(int i = 0; i < instructions.length; i++) {
			int id = instructions[i];
			AxilMethod method = methods.get(id);
			
			int[] args;
			if(method.getArgsLength() != -1) args = Arrays.copyOfRange(instructions, i + 1, i + method.getArgsLength() + 1);
			else args = Arrays.copyOfRange(instructions, i + 1, indexOf(Arrays.copyOfRange(instructions, i + 1, instructions.length), -1) + 2);
			
			int index = method.execute(args, memory) - 1;
			if(index >= 0) i = index;
			else i += args.length;
		}
	}
	
	public void call(String name, Object... parameters) {
		AxilFunction function = memory.getFunction(name);
		if(function == null) {
			System.err.println("FUNCTION \"" +  name + "\" DOES NOT EXIST");
			return;
		}
		
		memory.setInt(function.getReturnAddress(), instructions.length);
		
		for(int i = 0; i < function.getArgAddresses().length; i++) {
			int argAddress = function.getArgAddresses()[i];
			Object param = parameters[i];
			if(param instanceof Boolean) memory.setBoolean(argAddress, (boolean) param);
			else if(param instanceof Float) memory.setFloat(argAddress, (float) param);
			else if(param instanceof Integer) memory.setInt(argAddress, (int) param);
			else if(param instanceof String) memory.setString(argAddress, (String) param);
		}
		
		for(int i = function.getAddress(); i < instructions.length; i++) {
			int id = instructions[i];
			AxilMethod method = methods.get(id);
			
			int[] args;
			if(method.getArgsLength() != -1) args = Arrays.copyOfRange(instructions, i + 1, i + method.getArgsLength() + 1);
			else args = Arrays.copyOfRange(instructions, i + 1, indexOf(Arrays.copyOfRange(instructions, i + 1, instructions.length), -1) + 2);
			
			int index = method.execute(args, memory) - 1;
			if(index >= 0) i = index;
			else i += args.length;
		}
	}
	
	public static void main(String[] args) {
		AxilScript script = new AxilScript("res/test.axil");
		script.compile();
		script.run();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				script.call("click", "Dalton");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		frame.setTitle("Click Me");
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	
	public static int indexOf(int[] array, int value) {
	    for(int i = 0; i < array.length; i++) {
	         if(array[i] == value) {
	             return i;
	         }
		}
		return -1;
	}

}
