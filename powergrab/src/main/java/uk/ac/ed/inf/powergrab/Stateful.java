package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import com.mapbox.geojson.Point;

public class Stateful extends Drone{
//	https://zhuanlan.zhihu.com/p/29390935
//	https://blog.csdn.net/google19890102/article/details/45395257
//1	https://blog.csdn.net/sinat_30046339/article/details/50918045
	
	public Stateful(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, stations, seed);
		points.add(Point.fromLngLat(longitude, latitude));
		

	}
	
	public void p() {
		int i=0;
		for (Station s : stations) {
			if (! "danger".equals(s.getSymbol())) {
//				allCitys.add(new City(s.getLatitude(), s.getLongitude()));
				System.out.printf("%d %f %f\n",i,s.getLatitude(), s.getLongitude());
				i++;
			}
		}
		
//		points.addAll(SimulatedAnnealing.run(stations));
	}

}
