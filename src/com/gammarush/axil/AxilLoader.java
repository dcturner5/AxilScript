package com.gammarush.axil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.compiler.memory.AxilConstant;
import com.gammarush.axil.memory.AxilFunction;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.memory.AxilType;
import com.gammarush.axil.methods.AxilMethodMap;

public class AxilLoader {
	
	private static final byte STOP_CODE = 0;
	private static final byte CONSTANT_CODE = 1;
	private static final byte FUNCTION_CODE = 2;
	private static final byte INSTRUCTION_CODE = 3;
	private static final byte BOOLEAN_CODE = 4;
	private static final byte FLOAT_CODE = 5;
	private static final byte INT_CODE = 6;
	private static final byte STRING_CODE = 7;
	
	public static String loadTextFile(String path) {
		String string = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = null;
			while((line = in.readLine()) != null) {
				string += line;
			}
			in.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + path + "'");
		}
		catch (IOException e) {
			System.out.println("Error reading file '" + path + "'");
		}
		
		return string;
	}
	
	public static AxilScript open(String path) {
		int[] instructions = new int[0];
		AxilMemory memory = new AxilMemory(1024);
		
		try {
			FileInputStream file = new FileInputStream(path);
            DataInputStream in = new DataInputStream(file);
            
            int status = STOP_CODE;
            while(in.available() > 0) {
            	if(status == STOP_CODE) {
            		status = in.readByte();
            		System.out.println("STATUS: " + status);
            	}
            	if(status == CONSTANT_CODE) {
            		int address = in.readShort();
            		int type = in.readByte();
            		System.out.print("[" + address + "] ");
            		if(type == BOOLEAN_CODE) {
            			memory.setBoolean(address, in.readBoolean());
            		}
            		if(type == FLOAT_CODE) {
            			memory.setFloat(address, in.readFloat());
            		}
            		if(type == INT_CODE) {
            			memory.setInt(address, in.readInt());
            		}
            		if(type == STRING_CODE) {
            			memory.setString(address, in.readUTF());
            		}
            		status = STOP_CODE;
            	}
            	if(status == FUNCTION_CODE) {
            		String name = in.readUTF();
            		int address = in.readShort();
            		int returnAddress = in.readShort();
            		ArrayList<Integer> argAddressList = new ArrayList<Integer>();
            		int argAddress = in.readShort();
            		while(argAddress != STOP_CODE) {
            			argAddressList.add(argAddress);
            			argAddress = in.readShort();
            		}
            		int[] argAddresses = new int[argAddressList.size()];
            		for(int i = 0; i < argAddressList.size(); i++) {
            			System.out.println(argAddressList.get(i) + ", ");
            			argAddresses[i] = argAddressList.get(i);
            		}
            		memory.setFunction(new AxilFunction(name, address, returnAddress, argAddresses));
            		status = STOP_CODE;
            	}
            	if(status == INSTRUCTION_CODE) {
            		ArrayList<Integer> instructionList = new ArrayList<Integer>();
            		while(in.available() > 0) {
            			instructionList.add((int) in.readShort());
            		}
            		instructions = new int[instructionList.size()];
            		for(int i = 0; i < instructionList.size(); i++) {
            			System.out.print(instructionList.get(i) + ", ");
            			instructions[i] = instructionList.get(i);
            		}
            	}
            }
            
            in.close();
            file.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return new AxilScript(instructions, memory, new AxilMethodMap());
	}
	
	public static void save(String path, int[] instructions, AxilCompilerMemory memory) {
		try {
			FileOutputStream file = new FileOutputStream(path.replace(".txt", "") + ".axil");
            DataOutputStream out = new DataOutputStream(file);
            
            for(AxilConstant c : memory.getConstants()) {
    			out.writeByte(CONSTANT_CODE);
    			out.writeShort(c.getAddress());
    			if(c.getType() == AxilType.BOOLEAN) {
    				out.writeByte(BOOLEAN_CODE);
    				out.writeBoolean((boolean) c.getValue());
    			}
    			if(c.getType() == AxilType.FLOAT) {
    				out.writeByte(FLOAT_CODE);
    				out.writeFloat((float) c.getValue());
    			}
    			if(c.getType() == AxilType.INT) {
    				out.writeByte(INT_CODE);
    				out.writeInt((int) c.getValue());
    			}
    			if(c.getType() == AxilType.STRING) {
    				out.writeByte(STRING_CODE);
    				out.writeUTF((String) c.getValue());
    			}
    		}
    		for(AxilFunction f : memory.getFunctions()) {
    			out.writeByte(FUNCTION_CODE);
    			out.writeUTF(f.getName());
    			out.writeShort(f.getAddress());
    			out.writeShort(f.getReturnAddress());
    			for(int i : f.getArgAddresses()) {
    				out.writeShort(i);
    			}
    			out.writeShort(STOP_CODE);
    		}
            
    		out.writeByte(INSTRUCTION_CODE);
            for(int i : instructions) {
            	out.writeShort(i);
            }
             
            out.close();
            file.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
