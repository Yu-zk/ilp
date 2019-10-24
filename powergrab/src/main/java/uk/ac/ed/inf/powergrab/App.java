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
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * Hello world!
 *
 */
public class App 
{

	private static FeatureCollection fc;
	public static void main( String[] args )
//  input:  15 09 2019 55.944425 -3.188396 5678 stateless
    {
        System.out.println( "Hello World!" );

        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String mode = args[6];
        ArrayList<Station> stations = buildMap(year, month, day);
        
        Stateless play = new Stateless(latitude, longitude, seed, stations);
        int j = 0;
        while(j < 250 && play.next()) {
        	j++;
        }
        System.out.println(play.out);
        System.out.println(toJson(play.points));
        
        
        wirteFile(String.format("dronetype-%s-%s-%s.txt", day, month, year),play.out);
        wirteFile(String.format("dronetype-%s-%s-%s.geojson", day, month, year),toJson(play.points));
        

        

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
