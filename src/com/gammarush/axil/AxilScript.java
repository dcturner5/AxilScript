package com.gammarush.axil;

import java.util.Arrays;

import com.gammarush.axil.compiler.AxilCompiler;
import com.gammarush.axil.compiler.memory.AxilCompilerMemory;
import com.gammarush.axil.memory.AxilMemory;
import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.MethodHashMap;

public class AxilScript {
	
	public static AxilMemory memory = new AxilMemory(256);
	public static MethodHashMap map = new MethodHashMap();

	public static void main(String[] args) {
		String raw = AxilLoader.load("res/test.axil");
		AxilCompilerMemory compilerMemory = new AxilCompilerMemory();
		int[] script = new AxilCompiler(map).compile(raw, compilerMemory);
		
		System.out.println("*****");
		for(int i : script) {
			System.out.print(i + ", ");
		}
		System.out.println("\n*****");
		
		/*map.execute("assign", new int[] {3, 0}, GLOBAL);
		System.out.println(GLOBAL[0]);
		map.execute("assign", new int[] {7, 1}, GLOBAL);
		System.out.println(GLOBAL[1]);
		map.execute("add", new int[] {0, 1, 0}, GLOBAL);
		System.out.println(GLOBAL[0]);*/
		
		/*GLOBAL[0] = 5;
		GLOBAL[1] = 2;
		GLOBAL[2] = 0;
		GLOBAL[3] = 1;*/
		
		//run(new int[] {1, 0, 1, 2, 0, 2, 65, 3, 65, 1, 65}, GLOBAL);
		/*run(new int[] {
					1, 0, 1, 4,
					0, 4, 65,
					0, 2, 73,
					6, 73, 65, 4,
					8, 4, 18, 24,
					1, 73, 3, 73,
					9, 10
		}, GLOBAL);*/
		
		/*GLOBAL[0] = 5;
		GLOBAL[1] = 2;
		GLOBAL[2] = 8;
		GLOBAL[3] = 100;
		GLOBAL[4] = -100;*/
		/*GLOBAL.add(5);
		GLOBAL.add(1);
		GLOBAL.add(8);
		GLOBAL.add(190);
		GLOBAL.add(-100);
		run(new int[] {
				1, 0, 1, 5,
				0, 5, 65,
				6, 65, 2, 5,
				8, 5, 15, 20,
				0, 3, 65,
				9, 25,
				0, 4, 65,
				9, 25,
				10, 65
		}, GLOBAL);*/
		
		/*GLOBAL[0] = 3;
		GLOBAL[1] = 2;
		GLOBAL[2] = 4;
		GLOBAL[3] = 1;
		GLOBAL[4] = 3;
		
		GLOBAL.add(3);
		GLOBAL.add(2);
		GLOBAL.add(4);
		GLOBAL.add(1);
		GLOBAL.add(3);*/
		
		/*run(new int[] {
				11, 0, 1, 2, 3, 5, -1,
				0, 5, 65,
				12, 65, 4, 6,
				10, 6
		}, GLOBAL);*/
		
		
		
		// compile expression "!2+3*(6-(9-1))**2" -> 2 1 2 0 2 4 0 3 12 6 5 5 3 6 7 3 9 7 8 1 5 8 10
		/*GLOBAL.setInt(1, 9);
		GLOBAL.setInt(2, 1);
		GLOBAL.setInt(4, 6);
		GLOBAL.setInt(6, 2);
		GLOBAL.setInt(9, 3);
		run(new int[] {
				2, 1, 2, 0,
				2, 4, 0, 3,
				12, 6, 5,
				5, 3, 6, 7,
				3, 9, 7, 8,
				1, 5, 8, 10,
				map.getId("print"), 10
				}, GLOBAL);*/
		
		compilerMemory.load(memory);
		run(script, memory);
		
	}
	
	public static void run(int[] instructions, AxilMemory memory) {
		for(int i = 0; i < instructions.length; i++) {
			int id = instructions[i];
			AxilMethod method = map.get(id);
			/*System.out.print(i + " " + id + " ");
			System.out.println(method.getName());*/
			
			int[] args;
			if(method.getArgsLength() != -1) {
				args = Arrays.copyOfRange(instructions, i + 1, i + method.getArgsLength() + 1);
			}
			else {
				args = Arrays.copyOfRange(instructions, i + 1, indexOf(Arrays.copyOfRange(instructions, i + 1, instructions.length), -1) + 2);
			}
			
			int index = method.execute(args, memory) - 1;
			if(index >= 0) {
				i = index;
			}
			else {
				i += args.length;
			}
		}
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
