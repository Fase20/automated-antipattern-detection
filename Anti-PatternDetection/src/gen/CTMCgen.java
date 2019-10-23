package gen;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import gen.OrderedProperties;

public class CTMCgen {

	public CTMCgen() {
		try (FileReader reader = new FileReader("./res/config/FX_System.cfg")) {
			OrderedProperties properties = new OrderedProperties();
			properties.load(reader);
			FileWriter fw = new FileWriter("./res/models/FX_System.sm");

			int n_TA = Integer.parseInt(properties.getProperty("n_TA")); // getting the total number of TA instances
			int n_TA_count = 0;
			if (n_TA>9) {
				n_TA_count = 2;
			}
			else {
				n_TA_count = 1;
			}
			int nthreads = Integer.parseInt(properties.getProperty("nthreads")); // getting the total number of threads

			int taReq = 3; // setting the starting point for TA requests
			int taServe = 3; // setting the starting point for serving TA

			fw.write("ctmc\n\n");

			// inserting all the user-modified parameters with their values to the model
			for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = properties.getProperty(name);
				if (!name.contains("//") && !name.contains("n_TA") && !name.contains("Utilization") && !name.contains("probPath") && !name.contains("typeOfAP") && !name.contains("typeOfRun")) {
					if (name.equals("nthreads") || name.equals("MAX_QUEUE_SIZE")) {
						fw.write("const int " + name + " = " + value + ";\n");
					} else {
						if (name.matches("ta\\d{"+n_TA_count+"}Rate")) {
							int digitExtractor = Integer.parseInt(name.replaceAll("\\D+",""));
							if (digitExtractor <= n_TA) {
								fw.write("const double " + name + " = " + value + ";\n");
							}
						}
						else {
							fw.write("const double " + name + " = " + value + ";\n");
						}
					}
				}
			}
			
			// creating the WS module
			fw.write("\nmodule RequestQueue\n" + "  q : [0..MAX_QUEUE_SIZE] init 0;\n" + "\n"
					+ "  [NewReq]    true -> reqRate : (q'=min(q+1,MAX_QUEUE_SIZE)); // req arrival: increase req queue size (or drop request)\n");

			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [ServeReq" + i + "] q>0 -> internalOpRate:(q'=q-1);                 // thread " + i
						+ " serves request\n");
			}

			fw.write("endmodule\n\n");
			
			fw.write("rewards \"droppedRequests\"\n  [NewReq] q=MAX_QUEUE_SIZE : 1;\nendrewards\n\n");
			
			fw.write("rewards \"numOfReqsHandled\"\n  [NewReq] q<MAX_QUEUE_SIZE : 1;\nendrewards\n\n");

			// creating the Application module
			fw.write("module Workflow1\n" + "  s1 : [0.." + (8 + nthreads) + "] init 0;\n" + "\n"
					+ "  // 1. Extract request from queue and establish request type\n"
					+ "  [ServeReq1] s1=0 -> pExpertMode:(s1'=1) + pFundamentalAnalysisMode:(s1'=" + ((8 + nthreads) - 1)
					+ ");\n" + "\n" + "  // 2. Handle \"expert mode\" request\n"
					+ "  // 2.1. Invoke external service(s) for the Market Watch operation\n"
					+ "  [MW1] s1=1 -> MWsucc*MWrate:(s1'=2) + (1-MWsucc)*MWrate:(s1'=" + (8 + nthreads) + "); \n" + "\n"
					+ "  // 2.2. Invoke internal component for the Technical Analysis operation\n");

			// creating TA requests
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [TA" + i + "Invoke1] s1=2 -> 1:(s1'=" + taReq + "); // thread 1"
						+ " invokes TA instance "+i+" if not in use\n");
				taReq++;
			}

			// serving TA requests
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [TA" + i + "Exec1]   s1=" + taServe + " -> pObjSatisfied:(s1'=" + (taReq + 2)
						+ ") + pObjNotSat:(s1'=1) + pObjNotSatHighVar:(s1'=" + taReq + "); // wait for TA to complete\n");
				taServe++;
			}

			// completing the rest of the application module
			fw.write("\n  // 2.3. Invoke internal component for the Alarm operation\n" + "  [Alarm1] s1=" + taReq
					+ " -> alarmRate:(s1'=" + (taReq + 1) + ");\n\n  // 2.4. Done - successful outcome\n" + "  [] s1="
					+ (taReq + 1) + " -> internalOpRate:(s1'=0);\n\n"
					+ "  // 2.5. Invoke external service(s) for the Order operation\n" + "  [Order1] s1=" + (taReq + 2)
					+ " -> OrderSucc*OrderRate:(s1'=" + (taReq + 3) + ") + (1-OrderSucc)*OrderRate:(s1'=" + (8 + nthreads)
					+ ");\n\n" + "  // 2.6. Invoke internal component for the notification operation\n" + "  [Notif1] s1="
					+ (taReq + 3) + " -> notifRate:(s1'=" + (taReq + 1)
					+ ");\n\n  // 3. Handle \"fundamental analysis\" request \n"
					+ "  // 3.1. Invoke external service(s) for the Fundamental Analysis operation\n" + "  [FA1] s1="
					+ ((8 + nthreads) - 1) + " -> FASucc*FARate*pEnterTrade:(s1'=" + (taReq + 2)
					+ ") + FASucc*FARate*(1-pEnterTrade):(s1'=" + (taReq + 1) + ") + (1-FASucc)*FARate:(s1'="
					+ (8 + nthreads) + "); \n"
					+ "  \n  // 4. Done - unsuccessful outcome (invocation of external service failed)\n" + "  [] s1="
					+ (8 + nthreads) + " -> internalOpRate:(s1'=0);\n" + "endmodule\n\n");

			// creating the rest threads (if any)
			if (nthreads >= 2) {
				fw.write("// Other workflow threads\n");
			}
			for (int i = 2; i <= nthreads; i++) {
				fw.write("module Workflow" + i + " = Workflow1 [s1=s" + i
						+ ", ServeReq1=ServeReq" + i + ", ");
				for (int j = 1; j <= nthreads; j++) {
					fw.write("TA" + j + "Invoke1=TA" + j + "Invoke" + i + ", ");
				}
				fw.write("\n                                                          ");
				for (int k = 1; k <= nthreads; k++) {
					//if (k == nthreads) {
						fw.write("TA" + k + "Exec1=TA" + k + "Exec" + i + ", ");
					//} else {
						//fw.write("TA" + k + "Exec1=TA" + k + "Exec" + i + ", ");
					//}
				}
				fw.write("MW1=MW" + i + ", ");
				fw.write("Alarm1=Alarm" + i + ", ");
				fw.write("Order1=Order" + i + ", ");
				fw.write("Notif1=Notif" + i + ", ");
				fw.write("FA1=FA" + i + "");
					
				fw.write("] endmodule\n");
			}

			// creating the TA instances
			fw.write("// Internal component Technical Analysis, instance 1\n" + "module TA1\n" + "  t1 : [0.." + nthreads
					+ "] init 0;\n");

			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [TA1Invoke" + i + "] t1=0 -> internalOpRate:(t1'=" + i + ");\n");
			}
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [TA1Exec" + i + "]   t1=" + i + " -> ta1Rate:(t1'=0);\n");
			}
			fw.write("endmodule\n\n");

			// creating the rest of the TA instances (if any)
			for (int i = 2; i <= n_TA; i++) {
				fw.write("module TA" + i + " = TA1 [t1=t" + i + ", ");
				for (int j = 1; j <= nthreads; j++) {
					fw.write("TA1Invoke" + j + "=TA" + i + "Invoke" + j + ", ");
				}
				fw.write("\n                         ");
				for (int k = 1; k <= nthreads; k++) {
					fw.write("TA1Exec" + k + "=TA" + i + "Exec" + k + ",");
				}
				fw.write(" ta1Rate=ta" + i + "Rate] endmodule\n");
			}

			// rewards
			
			//"MWcount"
			fw.write("rewards \"MWcount\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [MW"+i+"] true : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"Notifcount"
			fw.write("rewards \"Notifcount\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [Notif"+i+"] true : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"Alarmcount"
			fw.write("rewards \"Alarmcount\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [Alarm"+i+"] true : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"FAcount"
			fw.write("rewards \"FAcount\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [FA"+i+"] true : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"Ordercount"
			fw.write("rewards \"Ordercount\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [Order"+i+"] s"+i+"="+(taReq + 2)+" : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//StatesForCount
			fw.write("label \"StatesForCount\" = ");
			if (nthreads==1) {
				fw.write("(s1=5|s1=9);\n\n");
			}
			else if (nthreads==2) {
				fw.write("(s1=6|s1=10|s2=6|s2=10);\n\n");
			}
			else {
				fw.write("(s1=7|s1=11|s2=7|s2=11|s3=7|s3=11);\n\n");
			}
			
			//"MWTAOrNoPath"
			fw.write("label \"MWTAOrNoPath\" = (s1=0|s1=1|s1=2|s1=3|");
			if (nthreads==1) {
				fw.write("s1=6|s1=7);\n\n");
				fw.write("label \"MWTAOrNoPath_END\" = s1=7;\n\n");
			}
			else if (nthreads==2) {
				fw.write("s1=4|s1=7|s1=8);\n\n");
				fw.write("label \"MWTAOrNoPath_END\" = s1=8;\n\n");
			}
			else {
				fw.write("s1=4|s1=5|s1=8|s1=9);\n\n");
				fw.write("label \"MWTAOrNoPath_END\" = s1=9;\n\n");
			}
			
			//"MWTAalarmPath"
			fw.write("label \"MWTAalarmPath\" = (s1=0|s1=1|s1=2|s1=4");
			if (nthreads==1) {
				fw.write(");\n\n");
				fw.write("label \"MWTAalarmPath_END\" = s1=4;\n\n");
			}
			else if (nthreads==2) {
				fw.write("|s1=5);\n\n");
				fw.write("label \"MWTAalarmPath_END\" = s1=5;\n\n");
			}
			else {
				fw.write("|s1=5|s1=6);\n\n");
				fw.write("label \"MWTAalarmPath_END\" = s1=6;\n\n");
			}
			
			//"FAOrNoPath"
			fw.write("label \"FAOrNoPath\" = (s1=0|");
			if (nthreads==1) {
				fw.write("s1=8|s1=6|s1=7);\n\n");
				fw.write("label \"FAOrNoPath_END\" = s1=7;\n\n");
			}
			else if (nthreads==2) {
				fw.write("s1=9|s1=7|s1=8);\n\n");
				fw.write("label \"FAOrNoPath_END\" = s1=8;\n\n");
			}
			else {
				fw.write("s1=10|s1=8|s1=9);\n\n");
				fw.write("label \"FAOrNoPath_END\" = s1=9;\n\n");
			}
			
			// "served"
			fw.write("rewards \"served\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [ServeReq" + i + "] true : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"extFails"
			fw.write("rewards \"extFails\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  s" + i + "=" + (8 + nthreads) + " : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"cost of the external components"
			fw.write("rewards \"extCompCost\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [MW"+i+"] s"+i+"=1  : MWcost;\n" + 
					 "  [Order"+i+"] s"+i+"="+(taReq + 2)+"  : Ordercost;\n" + 
					 "  [FA"+i+"] s"+i+"="+((8+nthreads)-1)+"  : FAcost;\n"); 
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}
			
			//"cost of the TA component"
			fw.write("rewards \"TAcost\"\n"); 
			for (int i=1; i<=n_TA; i++) {
				for (int j=1; j<=nthreads; j++) {
					fw.write("  [TA"+i+"Invoke"+j+"] s"+j+"=2  : TAcost;\n");
				}
			}
			fw.write("endrewards\n\n");
			
			//"overall cost"
			fw.write("rewards \"overallCost\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  [MW"+i+"] s"+i+"=1  : MWcost;\n" + 
					 "  [Order"+i+"] s"+i+"="+(taReq + 2)+"  : Ordercost;\n" + 
					 "  [FA"+i+"] s"+i+"="+((8+nthreads)-1)+"  : FAcost;\n"); 
			}
			for (int i=1; i<=n_TA; i++) {
				for (int j=1; j<=nthreads; j++) {
					fw.write("  [TA"+i+"Invoke"+j+"] s"+j+"=2  : TAcost;\n");
				}
			}
			fw.write("endrewards\n\n");

			// "processing time"
			fw.write("rewards \"processingTime\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  s" + i + ">0 : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}

			// "idle"
			fw.write("rewards \"idle\"\n");
			for (int i = 1; i <= nthreads; i++) {
				fw.write("  s" + i + "=0 : 1;\n");
				if (i == nthreads) {
					fw.write("endrewards\n\n");
				}
			}

			// "qLen"
			fw.write("rewards \"qLen\"\n" + "  true : q;\n" + "endrewards\n\n");
			
			//"servedTA"
			fw.write("rewards \"servedTA\"\n");
			for (int i=1; i<=n_TA; i++) {
				for (int j=1; j<=nthreads; j++) {
					fw.write("  [TA"+i+"Invoke"+j+"] true : 1;\n");
				}
			}
			fw.write("endrewards\n\n");
			
			//"taTime"
			fw.write("rewards \"taTime\"\n");
			for (int i=1; i<=nthreads; i++) {
				fw.write("  s"+i+"=2 : 1;\n" + 
						 "  s"+i+"=3 : 1;\n");
			}
			fw.write("endrewards\n");

			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
