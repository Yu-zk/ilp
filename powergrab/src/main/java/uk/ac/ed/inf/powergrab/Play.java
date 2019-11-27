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
		System.out.printf("%s %s %s\n",year,month,day);
		if ("stateful".equals(mode)) {
			Stateful play = new Stateful(latitude, longitude, seed, stations);
//			play.run();
			play.astar(new Position(55.9460,-3.1866), new Position(55.9446,-3.1877));
			wirteFile(String.format("stateful-%s-%s-%s.txt", day, month, year),play.getOut());
			wirteFile(String.format("stateful-%s-%s-%s.geojson", day, month, year),toJson(play.getPoints()));
//	        System.out.println(play.getOut());
		}else {
			Stateless play = new Stateless(latitude, longitude, seed, stations);
			play.run();
			wirteFile(String.format("stateless-%s-%s-%s.txt", day, month, year),play.getOut());
			wirteFile(String.format("stateless-%s-%s-%s.geojson", day, month, year),toJson(play.getPoints()));
//	        System.out.println(play.getOut());
		}
//        for (Station s:stations) {
//        	if ("lighthouse".equals(s.getSymbol())){
//        		System.out.printf("{%f,%f},",s.getLatitude(),s.getLongitude());
//        	}
//        	
//        }
//        ArrayList<Point> p=new ArrayList<Point> ();
//        double[][] l= {{5.5945293E7, -3191184.0},{5.5945277E7, -3192462.0},{5.5944531E7, -3191633.0},{5.5944497E7, -3191544.0},{5.5944048E7, -3191716.0},{5.5943905E7, -3191433.0},{5.5943753E7, -3191248.0},{5.594402E7, -3190458.0},{5.594273E7, -3191075.0},{5.5942662E7, -3189862.0},{5.5944216E7, -3186672.0},{5.5945177E7, -3185937.0},{5.5945009E7, -3185952.0},{5.5943819E7, -3185592.0},{5.5943868E7, -3186409.0},{5.5942645E7, -3186997.0},{5.5942942E7, -3185567.0},{5.5943925E7, -3184608.0},{5.5945066E7, -3184791.0},{5.5945817E7, -3184383.0},{5.5945487E7, -3185865.0},{5.5945753E7, -3187607.0},{5.594562E7, -3188837.0},{5.5945696E7, -3189096.0},{5.5945252E7, -3190341.0},{5.5944654E7, -3190303.0},{5.5944685E7, -3189689.0},{5.5944543E7, -3190036.0},{5.5943412E7, -3189071.0},{5.5944018E7, -3188127.0},{5.594503E7, -3187153.0}};
//        for (double[] i :l) {
//    		i[0]=i[0]/1000000;
//    		i[1]=i[1]/1000000;
//        }
//        for (double[] i :l) {
//        	p.add(Point.fromLngLat(i[1], i[0]));
//        }
//
//        wirteFile(String.format("dronetype-%s-%s-%s.geojson", day, month, year),toJson(p));
		//insure stations is not null
		

		
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
