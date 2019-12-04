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
/**
 * Download the data from the website, process them and write the txt and geojson file.
 *
 */
public class Play {
	private static FeatureCollection fc; //a featureCollection to contain all information in the geojson
	
	/**
	 * Constructor.
	 * Download the geojson file with the specified year, month and day and put all information into a Station ArrayList.
	 * Then, create an instance Stateless or Stateful depends on the input mode.
	 * Finally, write the log for each movement and geojson with the path with the specific file name.
	 */
	public Play(String day, String month, String year, double latitude, double longitude, int seed, String mode) {
		ArrayList<Station> stations = buildMap(year, month, day);
//		System.out.printf("%s %s %s ",year,month,day);
		if ("stateful".equals(mode)) {
			Stateful stateful = new Stateful(latitude, longitude, seed, stations);
			stateful.run();
			wirteFile(String.format("stateful-%s-%s-%s.txt", day, month, year),stateful.getOutput());
			wirteFile(String.format("stateful-%s-%s-%s.geojson", day, month, year),toJson(stateful.getPoints()));
		}else {
			Stateless stateless = new Stateless(latitude, longitude, seed, stations);
			stateless.run();
			wirteFile(String.format("stateless-%s-%s-%s.txt", day, month, year),stateless.getOutput());
			wirteFile(String.format("stateless-%s-%s-%s.geojson", day, month, year),toJson(stateless.getPoints()));
		}
//		System.out.println();
	}

	/**
	 * Write content into a file with a specific file name.
	 * @param fileName - The name of file is written
	 * @param content - The content in the file
	 */
	private static void wirteFile(String fileName, String content)  {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Add a LineString feature with all points in arraylist p at the end of featurecollection. 
	 * Then return geojson file as a string.
	 * @param p - A list of points
	 * @return
	 */
	private static String toJson(ArrayList<Point> p) {
		LineString ls = LineString.fromLngLats(p);
		Feature f = Feature.fromGeometry(ls);
		List<Feature> features = fc.features();
		features.add(f);
		FeatureCollection geojson = FeatureCollection.fromFeatures(features);
		return geojson.toJson();
	}

	/**
	 * Download the information as a string from a geojson file with the specified year, month and day.
	 * @param year - The year of the geojson file to be downloaded.
	 * @param month - The month of the geojson file to be downloaded.
	 * @param day- The day of the geojson file to be downloaded.
	 * @return Everything in the specific geojson file as a string
	 * @throws MalformedURLException If we cannot download anything.
	 */
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapSource;
	}

	/**
	 * Build an arraylist of station with the specified year, month and day.
	 * @param year - The year of the station list to be built.
	 * @param month - The month of the station list to be built.
	 * @param day- The day of the station list to be built.
	 * @return An array list of station on the specified date
	 */
	private static ArrayList<Station> buildMap(String year, String month, String day){
		String mapSource = "";
		ArrayList<Station> stations = new ArrayList<Station>();
		try {
			//Download
			mapSource = downloadInfo(year, month, day);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//use FeatureCollection to store the information from geojson file
		fc = FeatureCollection.fromJson(mapSource);
		List<Feature> f = fc.features();
		for (Feature i: f) {
			Symbol symbol;
			//use an enum class for the symbol since the enum comparison is faster than String.equal()
			if ("lighthouse".equals(i.getProperty("marker-symbol").getAsString())) {
				symbol = Symbol.lighthouse;
			}else {
				symbol = Symbol.danger;
			}
			stations.add(new Station(i.getProperty("id").getAsString(),
					i.getProperty("coins").getAsFloat(), 
					i.getProperty("power").getAsFloat(),
					symbol,
					i.getProperty("marker-color").getAsString(),
					((Point)i.geometry()).latitude(),
					((Point)i.geometry()).longitude()
					));
		}
		return stations;
	}

}
