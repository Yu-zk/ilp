package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import com.mapbox.geojson.Point;

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
	}
	
	/**
	 * Make a move. 
	 * @return true if the power is not negative; false otherwise.
	 */
	private boolean next() {
		if (setPower(-1.25)!=1.25) {
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
			update(availableStations.get(nextD).getId());
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
		out = out + sb.toString();
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
				double minDistance = 1;
				Station closestStation = null;
				for (Station s : stations) {
					double distance=s.distance(p1);
					if (distance<0.00025&&distance<minDistance) {
						minDistance = distance;
						closestStation=s;
					}
				}
				r.put(d, closestStation);
			}
			
		}
		return r;
	}
	
	/**
	 * Choose a direction which can arrive in the station the maximum power, 
	 * if there are many stations with the maximum power, choose one randomly.
	 * @param availableStations - a hash map which direction is the key and station is the value
	 * @param directions - an arraylist of directions we can choose
	 * @return the direction is chosen randomly
	 */
	private Direction chososeDirection(HashMap<Direction, Station> availableStations) {
		ArrayList<Direction> maxDirection = new ArrayList<Direction>();
		double max = -126;
		for (Direction d : availableStations.keySet()) {
			double power=0;
			if (availableStations.get(d) != null) {
				power = availableStations.get(d).getPower();
			}
			if (power > max) {
				max=power;
				maxDirection.clear();
				maxDirection.add(d);
			}else if (power == max) {
				maxDirection.add(d);
			}
		}
		return maxDirection.get(rnd.nextInt(maxDirection.size()));
	}
	
	/**
	 * update the coins and power for both drone and station with the given station id
	 * @param id - id of the station
	 */
	private void update(String id) {
		for (Station s : stations) {
			if (s.getId()==id) {
				s.update(setCoins(s.getCoins()),setPower(s.getPower()));
				break;
			}
		}
	}
	
}

