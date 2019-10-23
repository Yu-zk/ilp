package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Stateless {
	public double latitude;
    public double longitude;
    public double coins;
    public double power;
    public ArrayList<Station> stations;
    private java.util.Random rnd;

	public Stateless(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.stations = stations;
		rnd = new Random(seed);
		coins = 0;
		power = 250;
	}
	
	public boolean next() {
//		rnd.nextInt(16);
		HashMap<Direction, Station> availableStation = findStations(latitude, longitude);
		Direction nextD;
		double coins = 0;
		double power = 0;
		if (availableStation.size()>0) {
			double max = 0;
			Direction maxD = Direction.N;
			for (Direction d : availableStation.keySet()) {
				if (availableStation.get(d).power > max) {
					max = availableStation.get(d).power;
					maxD = d;
					coins = availableStation.get(d).coins;
					power = availableStation.get(d).power;
				}
			}
			if (max>0) {
				nextD = maxD;
				
			}else {
				nextD = Direction.values()[rnd.nextInt(16)];
				
			}
			
		}else {
			nextD = Direction.values()[rnd.nextInt(16)];
		}
		Position p = new Position(latitude, longitude);
		System.out.println(nextD);
		p = p.nextPosition(nextD);
		latitude = p.latitude;
		longitude = p.longitude;
		
		setCoins(coins);
		setPower(power);
		return this.power>0;
	}
	
	private void setCoins(double coins) {
		this.coins = this.coins + coins;
		if (this.coins <= 0) {
			this.coins =  0;
		}
	}

	private void setPower(double power) {
		this.power = power;
	}

	private HashMap<Direction, Station> findStations(double latitude, double longitude){
		HashMap<Direction, Station> r = new HashMap<Direction, Station>();
		Position p = new Position(latitude, longitude);
		for (Direction d : Direction.values()) {
			Position p1 = p.nextPosition(d);
			ArrayList<Station> ss = new ArrayList<Station>();
			for (Station s : stations) {
				
				if (distance(s.latitude, s.longitude, p1.latitude, p1.longitude) <= 0.00025){
					ss.add(s);
				}
			}
		    if (ss.size()==1) {
		    	r.put(d, ss.get(0));
		    }else if(ss.size()>1) {
		    	//algorithm
		    	r.put(d, ss.get(0));
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
