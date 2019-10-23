package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class processBuilder {
	
	private String choice;
	public static String currentAntipattern;
	public processBuilder(int selection) {
		if (selection == 1) {
			choice="./res/scripts/storm_marketWatch.sh";
		} else if (selection == 2) {
			choice="./res/scripts/storm_fundamentalAnalysis.sh";
		} else if (selection == 3) {
			choice="./res/scripts/storm_order.sh";
		} else {
			choice="./res/scripts/storm_CTMC.sh";
		}
		ProcessBuilder pb = new ProcessBuilder(choice);
		
		try {
			
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				if (selection == 1 || selection == 2 || selection == 3)
					builder.append(System.getProperty("line.separator"));
				else
					builder.append(",");
			}
			builder.setLength(builder.length() - 1);
			String result = builder.toString();
			
			currentAntipattern = result;
			
		} catch (IOException e) {
			System.out.print("error");
			e.printStackTrace();
		}

	}

}
