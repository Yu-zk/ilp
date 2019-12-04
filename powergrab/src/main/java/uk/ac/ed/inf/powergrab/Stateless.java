package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import com.mapbox.geojson.Point;
/**
 * An instance of this class is used to perform a stateless simulator.
 *
 */
public class Stateless extends Drone {
	
	/**
     * Constructor to create a new stateless drone instance by the constructor of superclass.
     * @param latitude - the latitude of this Drone
     * @param longitude - the longitude of this Drone
     * @param seed - the random seed
     * @param stations - the list of all stations
     */
	public Stateless(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, seed, stations);
		points.add(Point.fromLngLat(longitude, latitude));
	}
	
	/**
	 * Run the stateless simulator. Make a move, if the number of move is less than 250 and power is positive.
	 */
	public void run() {
        while(step < 250 && next()) {
        	step++;
        }
//        System.out.printf("%f %f ",coins,power);
	}
	
	/**
	 * Make a move. 
	 * @return true if the power is not negative; false otherwise.
	 */
	private boolean next() {
		if (setPower(-1.25f)!=1.25f) {
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(currentPosition.latitude);
		sb.append(",");
		sb.append(currentPosition.longitude);
		sb.append(",");
		HashMap<Direction, Station> availableStations = findStations();
		Direction nextD = chososeDirection(availableStations);
		
		// if the drone is close enough to a charging station after the move, 
		// update the coins and power for both drone and station
		if (availableStations.get(nextD)!=null) {
			updateWithId(availableStations.get(nextD).id);
		}
		
		currentPosition = currentPosition.nextPosition(nextD);
		sb.append(nextD);
		sb.append(",");
		sb.append(currentPosition.latitude);
		sb.append(",");
		sb.append(currentPosition.longitude);
		sb.append(",");
		sb.append(coins);
		sb.append(",");
		sb.append(power);
		sb.append("\n");
		output = output + sb.toString();
		points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		return this.power>0;
	}

	/**
	 * Return a hash map contains all directions in the key set and the nearest available(distance<0.00025) station.
	 * If there is no station within 0.00025, the value is null.
	 * @return a hash map which direction is the key and station is the value
	 */
	private HashMap<Direction, Station> findStations(){
		HashMap<Direction, Station> r = new HashMap<Direction, Station>();
		for (Direction d : Direction.values()) {
			Position p1 = currentPosition.nextPosition(d);
			if (p1.inPlayArea()) {
				r.put(d, nearestChargableStation(p1));
			}
		}
		return r;
	}
	
	/**
	 * Choose a direction which can arrive in the station with the maximum coin.
	 * If there are many stations with the maximum coin, 
	 * choose the direction which can go to the station with the maximum power
	 * If there are still more than one options, choose one randomly.
	 * @param availableStations - a hash map which direction is the key and station is the value
	 * @param directions - an arraylist of directions we can choose
	 * @return the direction is chosen randomly
	 */
	private Direction chososeDirection(HashMap<Direction, Station> availableStations) {
		ArrayList<Direction> maxCoinDirection = new ArrayList<Direction>();
		double max = -126;
		for (Direction d : availableStations.keySet()) {
			double coin=0;
			if (availableStations.get(d) != null) {
				coin = availableStations.get(d).getCoins();
			}
			if (coin > max) {
				max=coin;
				maxCoinDirection.clear();
				maxCoinDirection.add(d);
			}else if (coin == max) {
				maxCoinDirection.add(d);
			}
		}
		if (maxCoinDirection.size()==1) {
			//if there is only one direction can go to the station with the maximum coin
			return maxCoinDirection.get(0);
		}else {
			//else choose the direction can go to the station with the maximum power
			max = -126;
			ArrayList<Direction> maxPowerDirection = new ArrayList<Direction>();
			for (Direction d : maxCoinDirection) {
				double power=0;
				if (availableStations.get(d) != null) {
					power = availableStations.get(d).getPower();
				}
				if (power > max) {
					max = power;
					maxPowerDirection.clear();
					maxPowerDirection.add(d);
				}else if (power == max) {
					maxPowerDirection.add(d);
				}
			}
			//if there are more than one direction can go to the station with the max coins and power,
			//choose one randomly
			return maxPowerDirection.get(rnd.nextInt(maxPowerDirection.size()));
		}
	}
}

