package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import com.mapbox.geojson.Point;


public class Stateless extends Drone {
	
	public Stateless(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, stations, seed);
		points.add(Point.fromLngLat(longitude, latitude));
	}
	public void run() {
        while(step < 250 && next()) {
        	step++;
        }
	}
	
	private boolean next() {
		if (setPower(-1.25)!=1.25) {
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(currentPosition.latitude);
		sb.append(",");
		sb.append(currentPosition.longitude);
		sb.append(",");
		HashMap<Direction, Station> availableStation = findStations(currentPosition);
		Direction nextD;
		String id = "";
		if (availableStation.size()>0) {
			ArrayList<Direction> lighthouse = new ArrayList<Direction>();
			ArrayList<Direction> zero = new ArrayList<Direction>();
			ArrayList<Direction> danger = new ArrayList<Direction>();
			for (Direction d : Direction.values()) {
				if (availableStation.containsKey(d)) {
					if (Symbol.lighthouse==availableStation.get(d).getSymbol()) {
						lighthouse.add(d);
					}else if (Symbol.danger==availableStation.get(d).getSymbol()) {
						danger.add(d);
					}else{
						zero.add(d);
					}
				}else {
					zero.add(d);
				}
				
			}
			if (lighthouse.size()>0) {
				nextD = randomDirection(lighthouse, currentPosition);
			}else if (zero.size()>0) {
				nextD = randomDirection(zero, currentPosition);
			}else {
				nextD = randomDirection(danger, currentPosition);
			}
			if (availableStation.containsKey(nextD)) {
				id = availableStation.get(nextD).getId();
			}
		}else {
			nextD = randomDirection(currentPosition);
		}
		
		currentPosition = currentPosition.nextPosition(nextD);

		sb.append(nextD);
		sb.append(",");
		sb.append(currentPosition.latitude);
		sb.append(",");
		sb.append(currentPosition.longitude);
		sb.append(",");
		for (Station s : stations) {
			if (s.getId()==id) {
				s.update(setCoins(s.getCoins()),setPower(s.getPower()));
				break;
			}
		}
		sb.append(coins);
		sb.append(",");
		sb.append(power);
		sb.append("\n");
		out = out + sb.toString();
		points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		return this.power>0;
	}

	private Direction randomDirection(ArrayList<Direction> directions, Position p) {
		Direction d = directions.get(rnd.nextInt(directions.size()));
		while (!p.nextPosition(d).inPlayArea()) {
			d = directions.get(rnd.nextInt(directions.size()));
		}
		return d;
	}
	
	private Direction randomDirection(Position p) {
		Direction d = Direction.values()[rnd.nextInt(16)];
		while (!p.nextPosition(d).inPlayArea()) {
			d = Direction.values()[rnd.nextInt(16)];
		}
		return d;
	}

	
	private HashMap<Direction, Station> findStations(Position p){
		HashMap<Direction, Station> r = new HashMap<Direction, Station>();
		for (Direction d : Direction.values()) {
			Position p1 = p.nextPosition(d);
			double minDistance = 1;
			Station closestStation = null;
			for (Station s : stations) {
				double distance=s.distance(p1);
				if (distance<0.00025&&distance<minDistance) {
					minDistance = distance;
					closestStation=s;
				}
			}
			if (minDistance<1) {
				r.put(d, closestStation);
			}
			
		}
		return r;
	}
	
}

