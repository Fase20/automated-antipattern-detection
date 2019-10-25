package main;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

//import gen.DTMCgen;
import gen.CTMCgen;

public class MainGen {

	public static int typeOfRun;
	public static int typeOfAP;
	public static int selection=4;
	public static int cases;
	public static int experiment;
	public static int startAP;
	public static int endAP;
	public static HashMap<Color, String> listOfantipatterns = new HashMap<Color, String>();
	public static List<Coordinates> coordinateList = new ArrayList<Coordinates>();
	private static final Pattern COMMA_DELIMITER = Pattern.compile(",\\s*");

	public static void main(String[] args) throws ConfigurationException, IOException {
		//create the graphs folder if it does not exist
		String fileName = "./graphs";

        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
		PropertiesConfiguration config;		
		for (int exp=1; exp<=5; exp++) {
			experiment=exp;
			if (exp==1) {
				config = new PropertiesConfiguration("./res/config/FX_System.cfg");
				config.setProperty("FARate", "1000/40.02");
				config.setProperty("MWrate", "1000/50.21");
				config.setProperty("ta1Rate", 3);
				config.setProperty("n_TA", 1);
				config.setProperty("nthreads", 1);
				config.save();
				config = new PropertiesConfiguration("./res/config/marketWatch.cfg");
				config.setProperty("operation", "SEQ");
				config.save();
			}
			else if (exp==2) {
				config = new PropertiesConfiguration("./res/config/FX_System.cfg");
				config.setProperty("ta1Rate", 6);
				config.save();
			}
			else if (exp==3) {
				config = new PropertiesConfiguration("./res/config/FX_System.cfg");
				config.setProperty("ta1Rate", 3);
				config.setProperty("n_TA", 2);
				config.setProperty("nthreads", 2);
				config.save();
			}
			else if (exp==4) {
				config = new PropertiesConfiguration("./res/config/FX_System.cfg");
				config.setProperty("n_TA", 1);
				config.setProperty("nthreads", 1);
				config.setProperty("MWrate", "1000/500");
				config.save();
				config = new PropertiesConfiguration("./res/config/marketWatch.cfg");
				config.setProperty("operation", "PAR");
				config.save();
			}
			else {
				config = new PropertiesConfiguration("./res/config/marketWatch.cfg");
				config.setProperty("operation", "SEQ");
				config.save();
				config = new PropertiesConfiguration("./res/config/FX_System.cfg");
				config.setProperty("FARate", "1000/400");
				config.setProperty("MWrate", "1000/50.21");
				config.save();
			}

			for (int caseN=1; caseN<=3; caseN++) {
				cases=caseN;
				if (caseN==1) {
					config = new PropertiesConfiguration("./res/config/FX_System.cfg");
					config.setProperty("pObjNotSat", 0.78);
					config.setProperty("pObjSatisfied", 0.21);
					config.save();
				}
				else if (caseN==2) {
					config = new PropertiesConfiguration("./res/config/FX_System.cfg");
					config.setProperty("pObjNotSat", 0.01);
					config.setProperty("pObjSatisfied", 0.48);
					config.save();
				}
				else {
					config = new PropertiesConfiguration("./res/config/FX_System.cfg");
					config.setProperty("pObjNotSat", 0.01);
					config.setProperty("pObjSatisfied", 0.98);
					config.save();
				}
				try {
					if (experiment==1) {
						startAP=1;
						endAP=3;
					}
					else {
						if (caseN==1) {
							startAP=1;
							endAP=2;
						}
						else if (caseN==2) {
							startAP=1;
							endAP=1;
						}
						else {
							startAP=3;
							endAP=3;
						}
					}
					for (int ap=startAP; ap<=endAP; ap++) {
						typeOfAP=ap;
						if (ap==1) {
							config = new PropertiesConfiguration("./res/config/FX_System.cfg");
							config.setProperty("typeOfAP", 1);
							config.save();
						}
						else if (ap==2) {
							config = new PropertiesConfiguration("./res/config/FX_System.cfg");
							config.setProperty("typeOfAP", 2);
							config.save();
						}
						else {
							config = new PropertiesConfiguration("./res/config/FX_System.cfg");
							config.setProperty("typeOfAP", 3);
							config.save();
						}
						for (int i=1; i<=1; i++) {
							typeOfRun=i;
							if (i==1) {
								config = new PropertiesConfiguration("./res/config/FX_System.cfg");
								config.setProperty("Utilization", 0.65);
								config.setProperty("probPath", 0.65);
								config.setProperty("typeOfRun", 1);
								config.save();
							}
							else if (i==2) {
								config = new PropertiesConfiguration("./res/config/FX_System.cfg");
								config.setProperty("Utilization", 0.715);
								config.setProperty("probPath", 0.715);
								config.setProperty("typeOfRun", 2);
								config.save();
							}
							else if (i==3){
								config = new PropertiesConfiguration("./res/config/FX_System.cfg");
								config.setProperty("Utilization", 0.78);
								config.setProperty("probPath", 0.78);
								config.setProperty("typeOfRun", 3);
								config.save();
							}
							else if (i==4) {
								config = new PropertiesConfiguration("./res/config/FX_System.cfg");
								config.setProperty("Utilization", 0.85);
								config.setProperty("probPath", 0.85);
								config.setProperty("typeOfRun", 4);
								config.save();
							}
							else {
								config = new PropertiesConfiguration("./res/config/FX_System.cfg");
								config.setProperty("Utilization", 0.95);
								config.setProperty("probPath", 0.95);
								config.setProperty("typeOfRun", 5);
								config.save();
							}


							try {

								double pExpertMode=0.01;
								double pEnterTrade=0.01;

								while  (pExpertMode<1) {

									BigDecimal bdexp = new BigDecimal(pExpertMode);
									bdexp = bdexp.setScale(2, RoundingMode.HALF_UP);
									bdexp.doubleValue();

									config = new PropertiesConfiguration("./res/config/FX_System.cfg");
									config.setProperty("pExpertMode", bdexp);
									config.save();

									while (pEnterTrade<1) {

										BigDecimal bdent = new BigDecimal(pEnterTrade);
										bdent = bdent.setScale(2, RoundingMode.HALF_UP);
										bdent.doubleValue();

										config = new PropertiesConfiguration("./res/config/FX_System.cfg");
										config.setProperty("pEnterTrade", bdent);
										config.save();

										new CTMCgen();
										new processBuilder(selection);

										double dexp = bdexp.doubleValue();
										double dent = bdent.doubleValue();
										Coordinates AP = new Coordinates(dexp,dent,processBuilder.currentAntipattern);  
										coordinateList.add(AP);
										System.out.println(AP);

										pEnterTrade=pEnterTrade+0.1;
									}

									pEnterTrade=0.01;

									pExpertMode=pExpertMode+0.1;

								}


							} catch (ConfigurationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							//JOptionPane.showMessageDialog (null, "The new model has been created!", "Success", JOptionPane.INFORMATION_MESSAGE);
							createGraph(coordinateList);
							coordinateList.clear();
						}
					}
				}catch (ConfigurationException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
		}
	}

	public static void createGraph(List<Coordinates> coordinateList) {

		// Create Chart
		XYChart chart = new XYChartBuilder().xAxisTitle("pExpertMode").yAxisTitle("pEnterTrade").width(1400).height(1200).title("Antipattern Detection").build();

		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
		chart.getStyler().setChartTitleVisible(true);
		chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
		//chart.getStyler().setMarkerSize(20);
		chart.getStyler().setYAxisMin((double) 0);
		chart.getStyler().setYAxisMax((double) 1);
		chart.getStyler().setXAxisMin((double) 0);
		chart.getStyler().setXAxisMax((double) 1);

		// Series

		Map<String, Set<Coordinates>> nameToCoordinates = coordinateList.stream()
				.flatMap(
						c -> COMMA_DELIMITER.splitAsStream(c.getName())
						.map(n -> new Coordinates(c.getX(), c.getY(), n)))
				.collect(Collectors.groupingBy(Coordinates::getName, Collectors.toSet()));

		nameToCoordinates.remove("No Antipatterns Detected!");

		double i=-0.009;

		for (Map.Entry<String, Set<Coordinates>> serie : nameToCoordinates.entrySet()) {
			List<Double> xData = new ArrayList<>();
			List<Double> yData = new ArrayList<>();

			for (Coordinates coord : serie.getValue()) {

				xData.add(coord.getX()+i);
				yData.add(coord.getY());

			}
			System.out.println(serie.getKey());

			i=i+0.0076;
			System.out.println(xData);
			System.out.println(yData);		    

			XYSeries series = chart.addSeries(serie.getKey(), xData, yData);

			if (serie.getKey().equals("BLOB(MW)")) {
				series.setMarkerColor(Color.BLUE);
				series.setMarker(SeriesMarkers.CIRCLE);
			}
			else if (serie.getKey().equals("BLOB(FA)")) {
				series.setMarkerColor(Color.GREEN);
				series.setMarker(SeriesMarkers.CIRCLE);
			}
			else if (serie.getKey().equals("BLOB(Order)")) {
				series.setMarkerColor(Color.RED);
				series.setMarker(SeriesMarkers.CIRCLE);
			}
			else if (serie.getKey().equals("BLOB(TA)")) {
				series.setMarkerColor(Color.DARK_GRAY);
				series.setMarker(SeriesMarkers.CIRCLE);
			}
			else if (serie.getKey().contains("CPS(TA)min")) {
				series.setMarkerColor(Color.MAGENTA);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(Notif)")) {
				series.setMarkerColor(Color.CYAN);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(MW)min")) {
				series.setMarkerColor(Color.BLUE);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(MW)max")) {
				series.setMarkerColor(Color.YELLOW);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(FA)min")) {
				series.setMarkerColor(Color.GREEN);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(FA)max")) {
				series.setMarkerColor(Color.ORANGE);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(Order)min")) {
				series.setMarkerColor(Color.RED);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(Order)max")) {
				series.setMarkerColor(Color.CYAN);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().contains("CPS(TA)max")) {
				series.setMarkerColor(Color.DARK_GRAY);
				series.setMarker(SeriesMarkers.SQUARE);
			}
			else if (serie.getKey().equals("P&F(MW/MWTAOrNo)")) {
				series.setMarkerColor(Color.BLUE);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(MW/MWTAalarm)")) {
				series.setMarkerColor(Color.CYAN);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(FA/FAOrNo)")) {
				series.setMarkerColor(Color.GREEN);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(Order/MWTAOrNo)")) {
				series.setMarkerColor(Color.ORANGE);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(Order/FAOrNo)")) {
				series.setMarkerColor(Color.RED);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(TA/MWTAOrNo)")) {
				series.setMarkerColor(Color.DARK_GRAY);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
			else if (serie.getKey().equals("P&F(TA/MWTAalarm)")) {
				series.setMarkerColor(Color.PINK);
				series.setMarker(SeriesMarkers.DIAMOND);
			}
		}
		try {
			if (experiment==1) {
				if (cases==1) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-BLOB-caseA", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-CPS-caseA", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-P&F-caseA", BitmapFormat.JPG);
					}
				}
				else if (cases==2) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-BLOB-caseB", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-CPS-caseB", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-P&F-caseB", BitmapFormat.JPG);
					}
				}
				else {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-BLOB-caseC", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-CPS-caseC", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/IS-P&F-caseC", BitmapFormat.JPG);
					}
				}
			}
			else if (experiment==2) {
				if (cases==1) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-BLOB-caseA", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-CPS-caseA", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-P&F-caseA", BitmapFormat.JPG);
					}
				}
				else if (cases==2) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-BLOB-caseB", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-CPS-caseB", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-P&F-caseB", BitmapFormat.JPG);
					}
				}
				else {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-BLOB-caseC", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-CPS-caseC", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R1-P&F-caseC", BitmapFormat.JPG);
					}
				}

			}
			else if (experiment==3) {
				if (cases==1) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-BLOB-caseA", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-CPS-caseA", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-P&F-caseA", BitmapFormat.JPG);
					}
				}
				else if (cases==2) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-BLOB-caseB", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-CPS-caseB", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-P&F-caseB", BitmapFormat.JPG);
					}
				}
				else {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-BLOB-caseC", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-CPS-caseC", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R2-P&F-caseC", BitmapFormat.JPG);
					}
				}

			}
			else if (experiment==4) {
				if (cases==1) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-BLOB-caseA", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-CPS-caseA", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-P&F-caseA", BitmapFormat.JPG);
					}
				}
				else if (cases==2) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-BLOB-caseB", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-CPS-caseB", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-P&F-caseB", BitmapFormat.JPG);
					}
				}
				else {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-BLOB-caseC", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-CPS-caseC", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R3-P&F-caseC", BitmapFormat.JPG);
					}
				}

			}
			else {
				if (cases==1) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-BLOB-caseA", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-CPS-caseA", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-P&F-caseA", BitmapFormat.JPG);
					}
				}
				else if (cases==2) {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-BLOB-caseB", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-CPS-caseB", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-P&F-caseB", BitmapFormat.JPG);
					}
				}
				else {
					if (typeOfAP==1) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-BLOB-caseC", BitmapFormat.JPG);

					}
					else if (typeOfAP==2) {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-CPS-caseC", BitmapFormat.JPG);
					}
					else {
						BitmapEncoder.saveBitmap(chart, "./graphs/R4-P&F-caseC", BitmapFormat.JPG);
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
