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
