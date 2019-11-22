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
					if ("lighthouse".equals(availableStation.get(d).getSymbol())) {
						lighthouse.add(d);
					}else if ("danger".equals(availableStation.get(d).getSymbol())) {
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
		System.out.print(sb);

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
			ArrayList<Station> ss = new ArrayList<Station>();
			for (Station s : stations) {
//				distance(s.getLatitude(), s.getLongitude(), p1.latitude, p1.longitude)
				if ((s.distance(p1) <= 0.00025) ){ //&& s.getSymbol()!="zero"
					ss.add(s);
				}
			}
			if (p1.inPlayArea()) {
				if (ss.size()==1) {
			    	r.put(d, ss.get(0));
			    }else if(ss.size()>1) {
//			    	find the min
			    	double minDistance = 1;
			    	Station minStation = null;
			    	for (Station s : stations) {
			    		double d1 = s.distance(p1);
//			    		distance(s.getLatitude(), s.getLongitude(), p1.latitude, p1.longitude) ;
						if (d1<minDistance) {
							minDistance = d1;
							minStation = s;
						}
			    	}
			    	r.put(d, minStation);
			    
//			    	Comparator<Station> c=new Comparator<Station>()  {
//
//						@Override
//						public int compare(Station o1, Station o2) {
//							double d = (distance(o1.getLatitude(), o1.getLongitude(), p1.latitude, p1.longitude) - 
//									distance(o2.getLatitude(), o2.getLongitude(), p1.latitude, p1.longitude));
//							if (d>0){
//								return 1;
//							}else if(d==0) {
//								return 0;
//							}else {
//								return -1;
//							}
//						}
//					};
//					Collections.sort(ss,c);
//			    	r.put(d, ss.get(0));
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
	
	
	
//	if (availableStation.size()>0) {
//		double max = 0;
//		Direction maxD = Direction.N;
//		for (Direction d : availableStation.keySet()) {
//			if (availableStation.get(d).power > max) {
//				max = availableStation.get(d).power;
//				maxD = d;
//				id = availableStation.get(d).id;
//			}
//		}
//		if (max>0) {
//			nextD = maxD;
//		}else {
//			nextD = randomDirection(p);
//			if (!(availableStation.size()==16)) {
//				while (availableStation.containsKey(nextD)){
//					nextD = randomDirection(p);
//				}
//			}
//			
//		}
//		
//	}else {
//		nextD = randomDirection(p);
//	}

}
