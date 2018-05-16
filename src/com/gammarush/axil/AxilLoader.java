package com.gammarush.axil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AxilLoader {
	
	public static String load(String path) {
		String string = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = null;
			while((line = in.readLine()) != null) {
				string += line;
			}
			in.close();
			
			if(string.length() > 0) {
				char first = string.charAt(0);
				if(first == '{') {
					string = string.substring(1, string.length() - 1);
				}
				//result = parseObject(string);
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Unable to open file '" + path + "'");
		}
		catch (IOException e) {
			System.out.println("Error reading file '" + path + "'");
		}
		
		return string;
	}

}