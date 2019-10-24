package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.mapbox.geojson.MultiPoint;
import com.mapbox.geojson.Point;

public class Stateless {
	public double latitude;
    public double longitude;
    public double coins;
    public double power;
    public ArrayList<Station> stations;
    private java.util.Random rnd;
    public String out = "";
    public ArrayList<Point> points = new ArrayList<Point>();


	public Stateless(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.stations = stations;
		points.add(Point.fromLngLat(longitude, latitude));
		rnd = new Random(seed);
		coins = 0;
		power = 250;
	}
	
	public boolean next() {
//		rnd.nextInt(16);
		setPower(1.25);
		HashMap<Direction, Station> availableStation = findStations(latitude, longitude);
		out = out + String.format("%f,%f,", latitude, longitude);
		Direction nextD;
		Position p = new Position(latitude, longitude);
		String id = "";
		if (availableStation.size()>0) {
			double max = 0;
			Direction maxD = Direction.N;
			for (Direction d : availableStation.keySet()) {
				if (availableStation.get(d).power > max) {
					max = availableStation.get(d).power;
					maxD = d;
					id = availableStation.get(d).id;
				}
			}
			if (max>0) {
				nextD = maxD;
			}else {
				nextD = randomDirection(p);
				if (!(availableStation.size()==16)) {
					while (availableStation.containsKey(nextD)){
						nextD = randomDirection(p);
					}
				}
				
			}
			
		}else {
			nextD = randomDirection(p);
		}
		System.out.print(nextD);
		p = p.nextPosition(nextD);
		latitude = p.latitude;
		longitude = p.longitude;
		out = out + String.format("%s,%f,%f,", nextD, latitude, longitude);
		for (Station s : stations) {
			if (s.id==id) {
				s.update(setCoins(s.coins),setPower(s.power));
				break;
			}
		}
		out = out + String.format("%f,%f\n", coins, power);
		points.add(Point.fromLngLat(longitude, latitude));
		return this.power>0;
	}
	
	private double setCoins(double coins) {
		double coinsBefore = this.coins;
		if (this.coins + coins <= 0) {
			this.coins =  0;
			return -coinsBefore;
		}else {
			this.coins = this.coins + coins;
			return -coins;
		}
		 
	}
	
	private Direction randomDirection(Position p) {
		Direction d = Direction.values()[rnd.nextInt(16)];
		while (!p.nextPosition(d).inPlayArea()) {
			d = Direction.values()[rnd.nextInt(16)];
		}
		return d;
	}

	private double setPower(double power) {
		double powerBefore = this.power;
		if (this.power - power <= 0) {
			this.power = 0;
			return -powerBefore;
		}else {
			this.power = this.power + power;
			return -power;
		}
	}

	private HashMap<Direction, Station> findStations(double latitude, double longitude){
		HashMap<Direction, Station> r = new HashMap<Direction, Station>();
		Position p = new Position(latitude, longitude);
		for (Direction d : Direction.values()) {
			Position p1 = p.nextPosition(d);
			ArrayList<Station> ss = new ArrayList<Station>();
			for (Station s : stations) {
				
				if ((distance(s.latitude, s.longitude, p1.latitude, p1.longitude) <= 0.00025) && s.coins!=0 && s.power!=0){
					ss.add(s);
				}
			}
			if (p1.inPlayArea()) {
				if (ss.size()==1) {
			    	r.put(d, ss.get(0));
			    }else if(ss.size()>1) {
			    	//algorithm
			    	r.put(d, ss.get(0));
			    }
			}
		    
		}
			
		
		return r;
		
	}
	
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	private double distance(Position p1, Position p2) {
		return Math.sqrt((p2.latitude - p1.latitude)*(p2.latitude - p1.latitude) + (p2.longitude-p1.longitude)*(p2.longitude-p1.longitude));
	}
	
	
    

}
