package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Point;
/**
 * A super class for the stateful and steless because they have many identical feature.
 *
 */
public class Drone {
	protected Position currentPosition;
	protected double coins;
    protected double power;
    protected ArrayList<Station> stations;
    protected java.util.Random rnd;
    protected String out;
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
		out = "";
		step = 0;
		points = new ArrayList<Point>();
	}

    /**
     * Sets the coins for the drone when it passes a station. (The coins cannot be negative)
     * @param coins - The number of coins of the station.
     * @return the number of change of coins of the station.
     */
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

    /**
     * Sets the power for the drone when it passes a station. (The power cannot be negative)
     * @param power - The number of power of the station.
     * @return the number of change of power of the station.
     */
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
	
	/**
	 * Returns the movement log for the drone as string.
	 * @return the movement log for the drone.
	 */
	public String getOut() {
		return out;
	}
	
	/**
	 * Returns the arraylist of passed points for the drone.
	 * @return the passed points for the dronee.
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}
}
