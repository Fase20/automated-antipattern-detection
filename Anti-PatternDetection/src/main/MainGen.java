package main;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	public static HashMap<Color, String> listOfantipatterns = new HashMap<Color, String>();
	public static List<Coordinates> coordinateList = new ArrayList<Coordinates>();
	private static final Pattern COMMA_DELIMITER = Pattern.compile(",\\s*");

	public static void main(String[] args) {

				PropertiesConfiguration config;		

				try {
					for (int ap=1; ap<=3; ap++) {
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
//			}
//		});
//
//		//frame properties and visualisation 
//		frame.setSize(300, 210);
//		frame.setResizable(false);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
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

		//Map<String, List<Coordinates>> newList = coordinateList.stream()
		//       .collect(Collectors.groupingBy(Coordinates::getName));

		Map<String, Set<Coordinates>> nameToCoordinates = coordinateList.stream()
				.flatMap(
						c -> COMMA_DELIMITER.splitAsStream(c.getName())
						.map(n -> new Coordinates(c.getX(), c.getY(), n)))
				.collect(Collectors.groupingBy(Coordinates::getName, Collectors.toSet()));

		nameToCoordinates.remove("No Antipatterns Detected!");

		//System.out.println(nameToCoordinates);

		double i=-0.009;
		//double xPlusi=0;

		//XYSeries series;
		for (Map.Entry<String, Set<Coordinates>> serie : nameToCoordinates.entrySet()) {
			List<Double> xData = new ArrayList<>();
			List<Double> yData = new ArrayList<>();

			for (Coordinates coord : serie.getValue()) {

				//		    	double xPlusi = coord.getX() + i;
				//		    	BigDecimal bdxPlusi = new BigDecimal(xPlusi);
				//		    	bdxPlusi = bdxPlusi.setScale(2, RoundingMode.HALF_UP);
				//		    	double dxPlusi = bdxPlusi.doubleValue();

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
			if (typeOfAP==1) {
				if (typeOfRun==1)
					BitmapEncoder.saveBitmap(chart, "./graphs/BLOB_0.65", BitmapFormat.JPG);
				else if (typeOfRun==2)
					BitmapEncoder.saveBitmap(chart, "./graphs/BLOB_0.715", BitmapFormat.JPG);
				else if (typeOfRun==3)
					BitmapEncoder.saveBitmap(chart, "./graphs/BLOB_0.78", BitmapFormat.JPG);
				else if (typeOfRun==4)
					BitmapEncoder.saveBitmap(chart, "./graphs/BLOB_0.85", BitmapFormat.JPG);
				else
					BitmapEncoder.saveBitmap(chart, "./graphs/BLOB_0.95", BitmapFormat.JPG);
			}
			else if (typeOfAP==2) {
				if (typeOfRun==1)
					BitmapEncoder.saveBitmap(chart, "./graphs/CPS_0.65", BitmapFormat.JPG);
				else if (typeOfRun==2)
					BitmapEncoder.saveBitmap(chart, "./graphs/CPS_0.715", BitmapFormat.JPG);
				else if (typeOfRun==3)
					BitmapEncoder.saveBitmap(chart, "./graphs/CPS_0.78", BitmapFormat.JPG);
				else if (typeOfRun==4)
					BitmapEncoder.saveBitmap(chart, "./graphs/CPS_0.85", BitmapFormat.JPG);
				else
					BitmapEncoder.saveBitmap(chart, "./graphs/CPS_0.95", BitmapFormat.JPG);
			}
			else {
				if (typeOfRun==1)
					BitmapEncoder.saveBitmap(chart, "./graphs/P&F_0.65", BitmapFormat.JPG);
				else if (typeOfRun==2)
					BitmapEncoder.saveBitmap(chart, "./graphs/P&F_0.715", BitmapFormat.JPG);
				else if (typeOfRun==3)
					BitmapEncoder.saveBitmap(chart, "./graphs/P&F_0.78", BitmapFormat.JPG);
				else if (typeOfRun==4)
					BitmapEncoder.saveBitmap(chart, "./graphs/P&F_0.85", BitmapFormat.JPG);
				else
					BitmapEncoder.saveBitmap(chart, "./graphs/P&F_0.95", BitmapFormat.JPG);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		Thread t = new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				new SwingWrapper<XYChart>(chart).displayChart();     
		//			}
		//
		//		});
		//		t.start();		
	}
}
