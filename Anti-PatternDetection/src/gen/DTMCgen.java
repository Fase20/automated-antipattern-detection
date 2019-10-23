package gen;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class DTMCgen {

	private String name;
	private int count;

	public DTMCgen(int selection) {
		// setting up the FileReader object
		if (selection == 1) {
			name = "marketWatch";
		} else if (selection == 2) {
			name = "fundamentalAnalysis";
		} else {
			name = "order";
		}
		try (FileReader reader = new FileReader("res/config/" + name + ".cfg")) {
			Properties properties = new Properties();
			properties.load(reader);

			FileWriter fw = new FileWriter("res/models/" + name + ".pm");
			String operation = properties.getProperty("operation");
			int n = Integer.parseInt(properties.getProperty("n"));
			fw.write("dtmc\n\n");
			for (int i = 1; i <= n; i++) {
				if (operation.equals("PROB") || operation.equals("prob")) {
					fw.write("const double x" + i + " = " + properties.getProperty("x" + i) + ";\n");
					fw.write("const double c" + i + " = " + properties.getProperty("c" + i) + ";\n");
					fw.write("const double rt" + i + " = " + properties.getProperty("rt" + i) + ";\n");
					fw.write("const double p" + i + " = " + properties.getProperty("p" + i) + ";\n\n");

				} else {
					fw.write("const double c" + i + " = " + properties.getProperty("c" + i) + ";\n");
					fw.write("const double rt" + i + " = " + properties.getProperty("rt" + i) + ";\n");
					fw.write("const double p" + i + " = " + properties.getProperty("p" + i) + ";\n\n");
				}
				if (i == n) {
					if (properties.getProperty("timeout") != null
							&& !(properties.getProperty("timeout").length() == 0)) {
						fw.write("const double timeout = " + properties.getProperty("timeout") + ";\n\n");
					}
					if (selection == 1) {
						fw.write("module MARKET_WATCH\n\n");
					} else if (selection == 2) {
						fw.write("module FUNDAMENTAL_ANALYSIS\n\n");
					} else {
						fw.write("module ORDER\n\n");
					}
					if (operation.equals("PAR") || operation.equals("par")) {
						fw.write("  s : [1.." + ((n * 2) + 3) + "] init 1;\n\n");
						count = n;
						for (int j = 1; j <= n; j++) {
							count++;
							if (j < n) {
								fw.write("  [] s=" + j + " -> p" + j + ":(s'=" + count + ") + (1-p" + j + "):(s'="
										+ (j + 1) + ");\n");
							} else {
								fw.write("  [] s=" + j + " -> p" + j + ":(s'=" + count + ") + (1-p" + j + "):(s'="
										+ ((n * 2) + 1) + ");\n\n");
								for (int k = n + 1; k <= n * 2; k++) {
									fw.write("  [] s=" + k + " -> (s'=" + ((n * 2) + 2) + ");\n");
								}
							}
						}
						fw.write("\n  [] s=" + ((n * 2) + 1) + " -> (s'=" + ((n * 2) + 3) + "); //failure\n");
						fw.write("  [] s=" + ((n * 2) + 2) + " -> (s'=" + ((n * 2) + 3) + "); //success\n");
						fw.write("  [] s=" + ((n * 2) + 3) + " -> (s'=" + ((n * 2) + 3) + "); //done\n");
						fw.write("endmodule\n\n");
						fw.write("label \"success\" = s=" + ((n * 2) + 2) + ";\n");
						fw.write("label \"done\" = s=" + ((n * 2) + 3) + ";\n\n");
						fw.write("rewards \"cost\"\n");
						fw.write("  s=1 : ");
						for (int r = 1; r <= n; r++) {
							if (r < n) {
								fw.write("c" + r + "+");
							} else {
								fw.write("c" + r + ";\n");
							}
							if (r == n) {
								fw.write("endrewards\n\n");
								fw.write("rewards \"rt\"\n");
								count = n + 1;
								for (r = 1; r <= n; r++) {
									fw.write("  s=" + count + "  : rt" + r + ";\n");
									count++;
								}
								if (properties.getProperty("timeout") != null
										&& !(properties.getProperty("timeout").length() == 0)) {
									fw.write("  s=" + ((n * 2) + 1) + "  : timeout;\n");
								}
								fw.write("endrewards");
							}
						}
					} else if (operation.equals("SEQ") || operation.equals("seq")) {
						fw.write("  s : [1.." + ((n * 3) + 3) + "] init 1;\n\n");
						count = n;
						for (int j = 1; j <= n; j++) {
							count++;
							fw.write("  [] s=" + j + " -> p" + j + ":(s'=" + count + ") + (1-p" + j + "):(s'="
									+ (++count) + ");\n");
						}
						count = 1;
						for (int k = n + 1; k <= n * 3; k++) {
							count++;
							fw.write("\n  [] s=" + k + " -> (s'=" + ((n * 3) + 1) + "); //Service success\n");
							k++;
							if (k < n * 3) {
								fw.write("  [] s=" + k + " -> (s'=" + count + "); //Failure, go to next impl\n");
							} else {
								fw.write("  [] s=" + k + " -> (s'=" + ((n * 3) + 2) + "); //Failure\n");
							}
						}
						fw.write("\n  [] s=" + ((n * 3) + 1) + " -> (s'=" + ((n * 3) + 3) + "); //success\n");
						fw.write("  [] s=" + ((n * 3) + 2) + " -> (s'=" + ((n * 3) + 3) + "); //failure\n");
						fw.write("  [] s=" + ((n * 3) + 3) + " -> (s'=" + ((n * 3) + 3) + "); //done\n");
						fw.write("endmodule\n\n");
						fw.write("label \"success\" = s=" + ((n * 3) + 1) + ";\n");
						fw.write("label \"done\" = s=" + ((n * 3) + 3) + ";\n\n");
						fw.write("rewards \"cost\"\n");
						for (int r = 1; r <= n; r++) {
							fw.write("  s=" + r + " : c" + r + ";\n");
							if (r == n) {
								fw.write("endrewards\n\n");
								fw.write("rewards \"rt\"\n");
								count = n;
								for (r = 1; r <= n; r++) {
									count++;
									fw.write("  s=" + count + " : rt" + r + ";\n");
									count++;
									if (r == n && (properties.getProperty("timeout") != null)
											&& !(properties.getProperty("timeout").length() == 0)) {
										count = n + 1;
										fw.write("  ");
										for (r = 1; r <= n; r++) {
											if (r < n) {
												count++;
												fw.write("s=" + count + "|");
												count++;
											} else {
												count++;
												fw.write("s=" + count + " : timeout;\n");
											}
										}
									}
								}
							}
						}
						fw.write("endrewards");
					} else if (operation.equals("PROB") || operation.equals("prob")) {
						fw.write("  s : [1.." + ((n * 2) + 4) + "] init 1;\n\n");
						fw.write("  [] s=1 -> ");
						count = 2;
						for (int j = 1; j <= n; j++) {
							if (j < n) {
								fw.write("x" + j + ":(s'=" + count + ") + ");
							} else {
								fw.write("x" + j + ":(s'=" + count + ");\n\n");
							}
							count++;
						}
						count = n + 1;
						for (int j = 2; j <= (n + 1); j++) {
							count++;
							if (j < (n + 1)) {
								fw.write("  [] s=" + j + " -> p" + (j-1) + ":(s'=" + count + ") + (1-p" + (j-1) + "):(s'="
										+ ((n * 2) + 2) + ");\n");
							} else {
								fw.write("  [] s=" + j + " -> p" + (j-1) + ":(s'=" + count + ") + (1-p" + (j-1) + "):(s'="
										+ ((n * 2) + 2) + ");\n\n");
							}
						}
						for (int k = n + 2; k <= ((n * 2) + 1); k++) {
							fw.write("  [] s=" + k + " -> (s'=" + ((n * 2) + 3) + ");\n");
						}
						fw.write("\n  [] s=" + ((n * 2) + 2) + " -> (s'=" + ((n * 2) + 4) + "); //failure\n");
						fw.write("  [] s=" + ((n * 2) + 3) + " -> (s'=" + ((n * 2) + 4) + "); //success\n");
						fw.write("  [] s=" + ((n * 2) + 4) + " -> (s'=" + ((n * 2) + 4) + "); //done\n");
						fw.write("endmodule\n\n");
						fw.write("label \"success\" = s=" + ((n * 2) + 3) + ";\n");
						fw.write("label \"done\" = s=" + ((n * 2) + 4) + ";\n\n");
						fw.write("rewards \"cost\"\n");
						for (int r = 2; r <= (n + 1); r++) {
							fw.write("  s=" + r + " : c" + (r - 1) + ";\n");
							if (r == (n + 1)) {
								fw.write("endrewards\n\n");
								fw.write("rewards \"rt\"\n");
								count = n + 2;
								for (r = 1; r <= n; r++) {
									fw.write("  s=" + count + " : rt" + r + ";\n");
									count++;
									if (r == n && (properties.getProperty("timeout") != null)
											&& !(properties.getProperty("timeout").length() == 0)) {
										fw.write("  s=" + ((n * 2) + 2) + " : timeout;\n");
									}
								}
							}
						}
						fw.write("endrewards");
					}
				}
			}

			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
