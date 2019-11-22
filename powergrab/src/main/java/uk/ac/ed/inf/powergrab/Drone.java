package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Point;

public class Drone {
	protected Position currentPosition;
	protected double coins;
    protected double power;
    protected ArrayList<Station> stations;
    protected java.util.Random rnd;
    protected String out = "";
    protected ArrayList<Point> points = new ArrayList<Point>();
    protected int step = 0;
    
    
    
    protected Drone(double latitude, double longitude, ArrayList<Station> stations, int seed) {
		this.currentPosition = new Position(latitude, longitude);
		this.stations = stations;
		rnd = new Random(seed);
		this.coins = 0;
		this.power = 250;
	}
    protected double setCoins(double coins) {
		double coinsBefore = this.coins;
		if (this.coins + coins <= 0) {
			this.coins =  0;
			return -coinsBefore;
		}else {
			this.coins = this.coins + coins;
			return -coins;
		}
	}



	protected double setPower(double power) {
		double powerBefore = this.power;
		if (this.power + power <= 0) {
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
