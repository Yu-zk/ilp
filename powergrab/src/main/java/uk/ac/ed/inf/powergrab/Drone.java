package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Point;

public class Drone {
	protected double latitude;
	protected double longitude;
	protected double coins;
    protected double power;
    protected ArrayList<Station> stations;
    protected java.util.Random rnd;
    protected String out = "";
    protected ArrayList<Point> points = new ArrayList<Point>();
    
    
    
    protected Drone(double latitude, double longitude, ArrayList<Station> stations, int seed) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.stations = stations;
		rnd = new Random(seed);
		this.coins = 0;
		this.power = 250;
	}



	protected double setPower(double power) {
		double powerBefore = this.power;
		if (this.power - power <= 0) {
			this.power = 0;
			return -powerBefore;
		}else {
			this.power = this.power + power;
			return -power;
		}
	}
	public String getOut() {
		return out;
	}
	public ArrayList<Point> getPoints() {
		return points;
	}

}
