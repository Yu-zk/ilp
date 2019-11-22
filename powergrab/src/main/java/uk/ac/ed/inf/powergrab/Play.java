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
		Stateful play = new Stateful(latitude, longitude, seed, stations);
//        Stateless play = new Stateless(latitude, longitude, seed, stations);
		//insure stations is not null
		

        play.run();
//        System.out.println(play.getOut());
//        System.out.println(toJson(play.getPoints()));
        

        
        wirteFile(String.format("dronetype-%s-%s-%s.txt", day, month, year),play.getOut());
//        System.out.println(play.getPoints().size());
        wirteFile(String.format("dronetype-%s-%s-%s.geojson", day, month, year),toJson(play.getPoints()));
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
    		stations.add(new Station(i.getProperty("id").getAsString(),
    				i.getProperty("coins").getAsDouble(), 
    				i.getProperty("power").getAsDouble(),
    				i.getProperty("marker-symbol").getAsString(), 
    				i.getProperty("marker-color").getAsString(),
    				((Point)i.geometry()).latitude(),
    				((Point)i.geometry()).longitude()
    				));
    	}
//    	System.out.print(stations);
		return stations;
    	
    }

}
