package com.gammarush.axil;

import java.util.Arrays;

import com.gammarush.axil.methods.AxilMethod;
import com.gammarush.axil.methods.MethodHashMap;

public class AxilScript {
	
	public static Object[] GLOBAL = new Object[256];
	public static MethodHashMap map = new MethodHashMap();

	public static void main(String[] args) {
		String string = AxilLoader.load("res/script.axil");
		
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
		GLOBAL[4] = -100;
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
		
		GLOBAL[0] = 3;
		GLOBAL[1] = 2;
		GLOBAL[2] = 4;
		GLOBAL[3] = 1;
		GLOBAL[4] = 3;
		run(new int[] {
				11, 0, 1, 2, 3, 5, -1,
				0, 5, 65,
				12, 65, 4, 6,
				10, 6
		}, GLOBAL);
	}
	
	public static void run(int[] instructions, Object[] storage) {
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
			
			int index = method.execute(args, storage) - 1;
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
