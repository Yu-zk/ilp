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
 * Download the data from the website, process them and wirte the txt and geojson file.
 *
 */
public class Play {
	private static FeatureCollection fc; //a featureCollection to contain all information in the geojson
	
	/**
	 * Constructor.
	 * Download the geojson file with the given year, month and day and put all information into a Station ArrayList.
	 * Then, run the simulator.
	 * Finally, write the log for each movement and geogson with the path with the specific file name.
	 */
	public Play(String day, String month, String year, double latitude, double longitude, int seed, String mode) {
		ArrayList<Station> stations = buildMap(year, month, day);
//		System.out.printf("%s %s %s",year,month,day);
		
		if ("stateful".equals(mode)) {
			Stateful stateful = new Stateful(latitude, longitude, seed, stations);
			stateful.run();
			wirteFile(String.format("stateful-%s-%s-%s.txt", day, month, year),stateful.getOut());
			wirteFile(String.format("stateful-%s-%s-%s.geojson", day, month, year),toJson(stateful.getPoints()));
		}else {
			Stateless stateless = new Stateless(latitude, longitude, seed, stations);
			stateless.run();
			wirteFile(String.format("stateless-%s-%s-%s.txt", day, month, year),stateless.getOut());
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
		mapSource="{\n" + 
				"  \"type\": \"FeatureCollection\",\n" + 
				"  \"features\": [\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"cd1f-4ac1-e5a2-3839-105c-4916\",\n" + 
				"        \"coins\": \"-56.59364703437971\",\n" + 
				"        \"power\": \"-103.60669601784366\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#a00000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1854133126721695,\n" + 
				"          55.943120800350805\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"0627-fe49-a139-9eb1-8def-58b5\",\n" + 
				"        \"coins\": \"12.85559786877334\",\n" + 
				"        \"power\": \"121.26760329334019\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#008600\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1843674386557104,\n" + 
				"          55.943059169034406\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"22fd-b30b-9fa0-9e5a-76f6-275d\",\n" + 
				"        \"coins\": \"29.22957840719219\",\n" + 
				"        \"power\": \"44.82103795405214\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004a00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1880350193220828,\n" + 
				"          55.945831053880454\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"63b1-38c1-ab4e-9c54-db78-f77f\",\n" + 
				"        \"coins\": \"-101.51354537563711\",\n" + 
				"        \"power\": \"-16.82596913220845\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#760000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.184654788819441,\n" + 
				"          55.94445305418801\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"28ac-909d-97d3-adbd-79d1-1124\",\n" + 
				"        \"coins\": \"39.204956410273354\",\n" + 
				"        \"power\": \"65.9411936170098\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#006900\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1887978315353394,\n" + 
				"          55.94372671992983\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"8044-fb1a-aefb-e071-2da7-2e25\",\n" + 
				"        \"coins\": \"-31.484994017727086\",\n" + 
				"        \"power\": \"-66.44890136478503\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#620000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188610076904297,\n" + 
				"          55.94398507261271\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"38a1-a31b-6f82-c1d7-509a-2070\",\n" + 
				"        \"coins\": \"90.4995267310293\",\n" + 
				"        \"power\": \"3.0088401231343487\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#005e00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1897876992049845,\n" + 
				"          55.94556249143575\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"b37a-1687-e6c6-cad1-b43c-9ef3\",\n" + 
				"        \"coins\": \"11.378780421988477\",\n" + 
				"        \"power\": \"84.45690162922097\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#006000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1883774273717047,\n" + 
				"          55.94439154837953\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"ca48-b96d-16d6-c68e-2f9c-7464\",\n" + 
				"        \"coins\": \"42.60821792132543\",\n" + 
				"        \"power\": \"108.25452551334759\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#009700\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.187696962797721,\n" + 
				"          55.94298322137705\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"46fa-1ced-a750-d805-ba66-f697\",\n" + 
				"        \"coins\": \"87.74649170105712\",\n" + 
				"        \"power\": \"107.16059194653123\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#00c300\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.186315647909447,\n" + 
				"          55.94465860107027\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"dae2-18de-b4e2-f66b-bafa-b168\",\n" + 
				"        \"coins\": \"123.72705516733393\",\n" + 
				"        \"power\": \"4.105755534179009\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#008000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.189512867528505,\n" + 
				"          55.94457833569298\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"2e45-e5b3-72f0-ce28-48c9-687b\",\n" + 
				"        \"coins\": \"2.998257395208634\",\n" + 
				"        \"power\": \"64.44807436389576\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004300\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1912471659627184,\n" + 
				"          55.9427027962681\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"e828-f4f7-5de0-cd1d-4437-1b77\",\n" + 
				"        \"coins\": \"26.558473767133535\",\n" + 
				"        \"power\": \"42.93415556904315\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004500\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1912552822733957,\n" + 
				"          55.94400137164337\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"311b-91d1-df86-80c9-a09a-4696\",\n" + 
				"        \"coins\": \"19.04558081861038\",\n" + 
				"        \"power\": \"72.89283591818366\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#005c00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1857752127538372,\n" + 
				"          55.9447871121066\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"ca57-c08f-98c6-116e-6fa4-5865\",\n" + 
				"        \"coins\": \"20.744027577606364\",\n" + 
				"        \"power\": \"48.776346066368646\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004600\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.186844754290278,\n" + 
				"          55.944615149638636\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"152b-e564-2fab-2cdf-075b-6a64\",\n" + 
				"        \"coins\": \"-29.1955138842417\",\n" + 
				"        \"power\": \"-96.08434921700734\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#7d0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1871197919841574,\n" + 
				"          55.94325542574071\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"f43d-e72e-8f38-d268-b099-5156\",\n" + 
				"        \"coins\": \"-42.97672449463252\",\n" + 
				"        \"power\": \"-1.1551059435870037\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#2c0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1878161430358887,\n" + 
				"          55.94466399121498\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"54a4-3f64-bc74-e32c-5d4d-4703\",\n" + 
				"        \"coins\": \"14.97962779955124\",\n" + 
				"        \"power\": \"24.33190595712495\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#002700\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188342595750893,\n" + 
				"          55.945568666661494\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"2d23-db5f-b2f2-5866-656d-2735\",\n" + 
				"        \"coins\": \"-32.48706104395993\",\n" + 
				"        \"power\": \"-97.58976840185703\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#820000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188878297805786,\n" + 
				"          55.944651975059976\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"351f-98e3-76c8-4877-7ad5-a09c\",\n" + 
				"        \"coins\": \"69.92001576877712\",\n" + 
				"        \"power\": \"42.83546084572\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#007100\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1852320353320502,\n" + 
				"          55.942852171898856\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"ed00-06c9-f542-27e9-fe30-245f\",\n" + 
				"        \"coins\": \"-104.90789823780887\",\n" + 
				"        \"power\": \"-28.993539110941338\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#860000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188878297805786,\n" + 
				"          55.9441472931839\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"1b66-5872-9f0a-fbc3-5738-1200\",\n" + 
				"        \"coins\": \"7.151799098325154\",\n" + 
				"        \"power\": \"119.321621655423\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#007e00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1895423895804176,\n" + 
				"          55.944841755586296\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"1897-0099-bbd5-9e9b-0c50-86fb\",\n" + 
				"        \"coins\": \"108.56988097924584\",\n" + 
				"        \"power\": \"85.29357103872218\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#00c200\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.185104915931375,\n" + 
				"          55.943361044159836\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"972d-2bac-95c0-deda-e5e7-4ace\",\n" + 
				"        \"coins\": \"18.52363435840253\",\n" + 
				"        \"power\": \"70.91088193714093\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#005900\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1922151624384445,\n" + 
				"          55.943811311907126\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"5e14-8e76-53ce-8a6d-97d2-de10\",\n" + 
				"        \"coins\": \"-54.23160820213205\",\n" + 
				"        \"power\": \"-49.42545074697197\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#680000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1849726781217074,\n" + 
				"          55.94548712728174\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"1355-286d-b838-db5c-0491-e3a1\",\n" + 
				"        \"coins\": \"108.70474075794402\",\n" + 
				"        \"power\": \"23.3977651315392\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#008400\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1921344995498657,\n" + 
				"          55.946174992991885\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"2a7f-6b04-2952-a408-13e0-7738\",\n" + 
				"        \"coins\": \"6.515905529079741\",\n" + 
				"        \"power\": \"115.90050638385159\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#007a00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188369357162862,\n" + 
				"          55.943241850119044\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"0bf1-46b0-db02-00b0-8b69-41ce\",\n" + 
				"        \"coins\": \"-70.3447070635638\",\n" + 
				"        \"power\": \"-63.63249774712972\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#860000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1877195835113525,\n" + 
				"          55.944369594344536\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"1926-1292-067c-f396-c038-4925\",\n" + 
				"        \"coins\": \"45.953346564464034\",\n" + 
				"        \"power\": \"32.1399815688523\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004e00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1876150890916115,\n" + 
				"          55.94324761477146\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"dd0e-1921-244a-4e07-9769-7ddb\",\n" + 
				"        \"coins\": \"-121.4520398059501\",\n" + 
				"        \"power\": \"-22.68370914801164\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#900000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1882882118225098,\n" + 
				"          55.94400309715417\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"76f6-fc39-5e0e-60b0-1572-4548\",\n" + 
				"        \"coins\": \"8.695031681303616\",\n" + 
				"        \"power\": \"80.26038038793635\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#005900\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1865711208187903,\n" + 
				"          55.94563622039803\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"e61f-6d2c-4619-8144-a199-3ff3\",\n" + 
				"        \"coins\": \"-34.91942650707336\",\n" + 
				"        \"power\": \"-39.55774202826995\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#4a0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.189082145690918,\n" + 
				"          55.94441765929257\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"95d3-10bd-1c48-b8c9-c2fd-9efc\",\n" + 
				"        \"coins\": \"81.23960119776281\",\n" + 
				"        \"power\": \"20.162322226445816\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#006500\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1869627632703517,\n" + 
				"          55.94457925182649\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"601b-d83d-0691-7e98-c392-d1dc\",\n" + 
				"        \"coins\": \"-17.083714984719375\",\n" + 
				"        \"power\": \"-64.53777147839567\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#520000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1846700588757013,\n" + 
				"          55.945368134165\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"07b8-13c0-9a3f-2b98-c57c-f4aa\",\n" + 
				"        \"coins\": \"-68.78552901602811\",\n" + 
				"        \"power\": \"-82.469078223462\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#970000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1885564327239986,\n" + 
				"          55.944880281367176\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"5666-d920-6493-8e65-492e-36e9\",\n" + 
				"        \"coins\": \"73.67778717667453\",\n" + 
				"        \"power\": \"111.15284402327664\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#00b900\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1869024162033655,\n" + 
				"          55.94413136925416\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"3e10-a875-1223-5704-c4d3-6417\",\n" + 
				"        \"coins\": \"64.7271736521838\",\n" + 
				"        \"power\": \"19.769129970990534\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#005400\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1907129287719727,\n" + 
				"          55.945677840863326\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"33a1-5e6d-7ea3-e7f9-09f4-0b05\",\n" + 
				"        \"coins\": \"57.990323575599675\",\n" + 
				"        \"power\": \"94.06962943738397\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#009800\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1905479478337972,\n" + 
				"          55.94477167143578\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"4eaa-6526-e41c-a03d-b94e-5c74\",\n" + 
				"        \"coins\": \"90.27923459166631\",\n" + 
				"        \"power\": \"68.80481549296036\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#009f00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.186519207721459,\n" + 
				"          55.94312796290692\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"7b0d-45cf-236a-0c94-7602-0425\",\n" + 
				"        \"coins\": \"74.17976160438475\",\n" + 
				"        \"power\": \"90.41233934739618\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#00a500\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.187993552098357,\n" + 
				"          55.945917296699065\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"eb52-6fc4-61a5-93e6-ec79-630c\",\n" + 
				"        \"coins\": \"90.03656086020125\",\n" + 
				"        \"power\": \"47.98328996597849\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#008a00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.18591059344834,\n" + 
				"          55.943629560466604\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"ffd9-d0f6-1293-567f-2750-5c3c\",\n" + 
				"        \"coins\": \"36.97250484354735\",\n" + 
				"        \"power\": \"104.31412289937322\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#008d00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.192215696482051,\n" + 
				"          55.943117762391395\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"03d2-813c-c4ec-fa66-13e1-67bb\",\n" + 
				"        \"coins\": \"83.16475375555095\",\n" + 
				"        \"power\": \"42.119271617577155\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#007d00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.185585843966065,\n" + 
				"          55.942699527522855\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"18d6-2afc-de17-d165-0b0b-06a9\",\n" + 
				"        \"coins\": \"-77.92749485175572\",\n" + 
				"        \"power\": \"-65.13942348739137\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#8f0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.185036608779986,\n" + 
				"          55.942916618265805\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"6bdc-e682-0679-a088-ba4f-1cba\",\n" + 
				"        \"coins\": \"51.44782720031721\",\n" + 
				"        \"power\": \"106.50770009493935\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#009e00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1865063385370545,\n" + 
				"          55.94600956307409\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"68e4-094b-30b2-63c8-4d9e-2904\",\n" + 
				"        \"coins\": \"-85.85949626186446\",\n" + 
				"        \"power\": \"-39.89390026384114\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#7e0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.187987804412842,\n" + 
				"          55.94418334210744\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"a6bf-18cf-049e-99fc-1692-7063\",\n" + 
				"        \"coins\": \"84.49909825082845\",\n" + 
				"        \"power\": \"15.555788233997129\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#006400\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.190544218185848,\n" + 
				"          55.94592215492642\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"6ca3-b4f9-89a3-87b8-ab03-4552\",\n" + 
				"        \"coins\": \"24.766730720488336\",\n" + 
				"        \"power\": \"51.23552745993068\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#004c00\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.1849694050264983,\n" + 
				"          55.943609204740625\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"d009-8c40-69f1-7f85-8f4e-592a\",\n" + 
				"        \"coins\": \"-6.4240719231069\",\n" + 
				"        \"power\": \"-70.19726882796238\",\n" + 
				"        \"marker-symbol\": \"danger\",\n" + 
				"        \"marker-color\": \"#4d0000\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.188159465789795,\n" + 
				"          55.94480818478398\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"type\": \"Feature\",\n" + 
				"      \"properties\": {\n" + 
				"        \"id\": \"da75-f137-4d61-7043-d742-adbb\",\n" + 
				"        \"coins\": \"83.66359770865472\",\n" + 
				"        \"power\": \"81.08245829245416\",\n" + 
				"        \"marker-symbol\": \"lighthouse\",\n" + 
				"        \"marker-color\": \"#00a500\"\n" + 
				"      },\n" + 
				"      \"geometry\": {\n" + 
				"        \"type\": \"Point\",\n" + 
				"        \"coordinates\": [\n" + 
				"          -3.184511326414979,\n" + 
				"          55.945566056187886\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}";
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
					i.getProperty("coins").getAsDouble(), 
					i.getProperty("power").getAsDouble(),
					symbol,
					i.getProperty("marker-color").getAsString(),
					((Point)i.geometry()).latitude(),
					((Point)i.geometry()).longitude()
					));
		}
		return stations;
	}

}
