package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Play {
	private static FeatureCollection fc;
	public Play(String day, String month, String year, double latitude, double longitude, int seed, String mode) {
//		super();
		ArrayList<Station> stations = buildMap(year, month, day);
//		System.out.printf("%s %s %s\n",year,month,day);
		if ("stateful".equals(mode)) {
//			StatefulOld play = new StatefulOld(latitude, longitude, seed, stations);
			Stateful play = new Stateful(latitude, longitude, seed, stations);
			play.run();
//			play.astar(new Position(55.9431,-3.1922), new Station( "id", 1.0,1.0,  Symbol.lighthouse,  "color",  55.9456,-3.1845));
			wirteFile(String.format("stateful-%s-%s-%s.txt", day, month, year),play.getOut());
			wirteFile(String.format("stateful-%s-%s-%s.geojson", day, month, year),toJson(play.getPoints()));
		}else {
			Stateless play = new Stateless(latitude, longitude, seed, stations);
			play.run();
			wirteFile(String.format("stateless-%s-%s-%s.txt", day, month, year),play.getOut());
			wirteFile(String.format("stateless-%s-%s-%s.geojson", day, month, year),toJson(play.getPoints()));
		}
//        for (Station s:stations) {
//        	if (Symbol.lighthouse==s.getSymbol()){
//        		System.out.printf("{%f,%f},",s.getLatitude(),s.getLongitude());
//        	}
//        	
//        }
//        ArrayList<Point> p=new ArrayList<Point> ();
//        double[][] l= {{55.944517, -3.185278},{55.944922, -3.185207},{55.945519, -3.184759},{55.945028, -3.18433},{55.943151, -3.184744},{55.943235, -3.185759},{55.942763, -3.186431},{55.94454, -3.187441},{55.943811, -3.187462},{55.943456, -3.187537},{55.943147, -3.188311},{55.942778, -3.189457},{55.943646, -3.189711},{55.94369, -3.189939},{55.943133, -3.190565},{55.94331, -3.190982},{55.942626, -3.191776},{55.942886, -3.192405},{55.943632, -3.191861},{55.944295, -3.192079},{55.943974, -3.190479},{55.945487, -3.190921},{55.945608, -3.190954},{55.946167, -3.190923},{55.945848, -3.190246},{55.945467, -3.190016},{55.94516, -3.189864},{55.945383, -3.189086},{55.945928, -3.188717},{55.946008, -3.18815},{55.945387, -3.188308},{55.945289, -3.187967},{55.94556, -3.187502},{55.945182, -3.186668},{55.944685, -3.187112},{55.944597, -3.186711},{55.944693, -3.186434}};
//
//        for (double[] i :l) {
//        	p.add(Point.fromLngLat(i[1], i[0]));
//        }
//
//        wirteFile(String.format("dronetype-%s-%s-%s.geojson", day, month, year),toJson(p));
		

		
//        System.out.println(toJson(play.getPoints()));

        
	}

	private static void wirteFile(String fileName, String str)  {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(str);
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
        
    private static String toJson(ArrayList<Point> p) {
    	LineString ls = LineString.fromLngLats(p);
        Feature f = Feature.fromGeometry(ls);
        List<Feature> features = fc.features();
        features.add(f);
        FeatureCollection geojson = FeatureCollection.fromFeatures(features);
        return geojson.toJson();
    }
    private static String downloadInfo(String year, String month, String day) throws MalformedURLException {
    	String mapString = String.format(
    			"http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
    			year, month, day);
    	String mapSource="";
    	try {
    		HttpURLConnection conn = (HttpURLConnection) new URL(mapString).openConnection();
    		conn.setReadTimeout(10000); 
    		conn.setConnectTimeout(15000); 
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);
    		conn.connect();
    		InputStream is = conn.getInputStream();
    		InputStreamReader isReader = new InputStreamReader(is);
    		BufferedReader reader = new BufferedReader(isReader);
    		StringBuffer sb = new StringBuffer();
    		String str;
    		while((str = reader.readLine())!= null){
    			sb.append(str);
    		}
    		mapSource = sb.toString();
//    		System.out.println(mapSource);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mapSource;
    }
    
    private static ArrayList<Station> buildMap(String year, String month, String day){
    	String mapSource="";
    	ArrayList<Station> stations = new ArrayList<Station>();
    	try {
    		mapSource = downloadInfo(year, month, day);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	fc = FeatureCollection.fromJson(mapSource);
    	List<Feature> f = fc.features();

    	for (Feature i:f) {
//    		String id = i.getProperty("id").getAsString();
//    		double coins = i.getProperty("coins").getAsDouble();
//    		double power = i.getProperty("power").getAsDouble();
//    		double latitude = ((Point)i.geometry()).latitude();
//    		double longitude = ((Point)i.geometry()).longitude();
    		Symbol symbol;
    		if ("lighthouse".equals(i.getProperty("marker-symbol").getAsString())) {
    			symbol=Symbol.lighthouse;
    		}else {
    			symbol=Symbol.danger;
    		}
    		stations.add(new Station(i.getProperty("id").getAsString(),
    				i.getProperty("coins").getAsDouble(), 
    				i.getProperty("power").getAsDouble(),
    				symbol,
    				i.getProperty("marker-color").getAsString(),
    				((Point)i.geometry()).latitude(),
    				((Point)i.geometry()).longitude()
    				));
    	}
//    	System.out.print(stations);
		return stations;
    	
    }

}
