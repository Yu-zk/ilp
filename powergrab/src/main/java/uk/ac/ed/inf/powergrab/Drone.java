package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Point;
/**
 * A class to represent a drone to store some information of the drone. Every Play can only have one Drone.
 * The super class for the stateful and steless because they have many identical features and methods.
 *
 */
public class Drone {
	protected Position currentPosition;
	protected float coins;
    protected float power;
    protected ArrayList<Station> stations;
    protected java.util.Random rnd;
    protected String output;
    protected ArrayList<Point> points;
    protected int step;
    
    /**
     * Constructor to create a new drone instance and sets the location to the specified double latitude and longitude.
     * It also set all stations in the map, a random seed and initialise coins to 0, power to 250, step to 0
     * an empty output string and an empty list of point.
     * @param latitude - the latitude of this Drone
     * @param longitude - the longitude of this Drone
     * @param seed - the random seed
     * @param stations - the list of all stations
     */
    protected Drone(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		this.currentPosition = new Position(latitude, longitude);
		this.stations = stations;
		rnd = new Random(seed);
		this.coins = 0;
		this.power = 250;
		output = "";
		step = 0;
		points = new ArrayList<Point>();
	}

    /**
     * Sets the coins for the drone when it passes a station. (The coins cannot be negative)
     * @param coins - The number of coins of the station.
     * @return the number of change of coins of the station.
     */
    protected float setCoins(float coins) {
    	float coinsBefore = this.coins;
		if (this.coins + coins <= 0) {
			this.coins =  0;
			return -coinsBefore;
		}else {
			this.coins = this.coins + coins;
			return -coins;
		}
	}

    /**
     * Sets the power for the drone when it passes a station. (The power cannot be negative)
     * @param power - The number of power of the station.
     * @return the number of change of power of the station.
     */
	protected float setPower(float power) {
		float powerBefore = this.power;
		if (this.power + power <= 0) {
			this.power = 0;
			return -powerBefore;
		}else {
			this.power = this.power + power;
			return -power;
		}
	}
	
	/**
	 * Return the station can be collected from the specified position, null if there is no such station
	 * @param p - a specified position
	 * @return the station can be collected from the specified position
	 */
	protected Station nearestChargableStation(Position p) {
		double min = 0.00025;
		Station nearestStation= null;
		for (Station s: stations) {
			double d = s.distance(p);
			if (d <= min) {
				min = d;
				nearestStation = s;
			}
		}
		return nearestStation;
	}
	
	/**
	 * Update the coins and power for both drone and station with the given station id.
	 * @param id - id of the station
	 */
	protected void updateWithId(String id) {
		for (Station s : stations) {
			if (s.id.equals(id)) {
				s.update(setCoins(s.getCoins()),setPower(s.getPower()));
				break;
			}
		}
	}
	
	/**
	 * Returns the movement log for the drone as string.
	 * @return the movement log for the drone.
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * Returns the arraylist of passed points for the drone.
	 * @return the passed points for the dronee.
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}
}
