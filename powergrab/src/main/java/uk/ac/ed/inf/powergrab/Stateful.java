package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.mapbox.geojson.Point;
/**
 * An instance of this class is used to perform a stateful simulator.
 *
 */
public class Stateful extends Drone{

	private ArrayList<Station> path;
	private ArrayList<Station> danger;
	private int tryTime;

	/**
     * Constructor to create a new statefull drone instance by the constructor of superclass.
     * Initialize  the list path which contains all stations with the symbol lighthouse,
     *   then create a path by the greedy algorithm.
     * Initialize  the list danger which contains all stations with the symbol danger,
     *   then sort it with the descending order of coins.
     * @param latitude - the latitude of this Drone
     * @param longitude - the longitude of this Drone
     * @param seed - the random seed
     * @param stations - the list of all stations
     */
	public Stateful(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, seed, stations);
		points.add(Point.fromLngLat(longitude, latitude));
		// find all stations with the positive coin and power, then create a sequence of steps
		path = new ArrayList<Station>();
		for (Station s:stations) {
			if (Symbol.lighthouse==s.getSymbol()) {
				path.add(s);
			}
		}
		path = findPath(path);
		
		// find all stations with the negative coin and power, then sort with the descending order of coins
		danger = new ArrayList<Station>();
		for (Station s:stations) {
			if (Symbol.danger==s.getSymbol()) {
				danger.add(s);
			}
		}
		Comparator<Station> c=new Comparator<Station>()  {
			@Override
			public int compare(Station o1, Station o2) {
				double d = (o1.getCoins()-o2.getCoins());
				if (d<0){
					return 1;
				}else if(d==0) {
					return 0;
				}else {
					return -1;
				}
			}
		};
		Collections.sort(danger,c);
		tryTime = 0;
	}
	
	/**
	 * Run the stateful simulator. Make a move, if the number of move is less than 250 and power is positive.
	 */
	public void run() {
		while(step < 250 && next()) {}
//		System.out.printf("%f %f ",coins,power);
	}
	
	/**
	 * Build a path with the shortest total distance by the greedy algorithm.
	 * @param original - an arraylist of the stations
	 * @return an arraylist of the stations with the order
	 */
	private ArrayList<Station> findPath(ArrayList<Station> original){
		ArrayList<Station> sorted = new ArrayList<Station>();
		Position p = currentPosition;
		Station nextS = null;
		while (original.size()>0) {
			double min=10;
			for (Station s: original) {
				double d=s.distance(p);
				if (!sorted.contains(s)&&d<min) {
					min = d;
					nextS = s;
				}
			}
			sorted.add(nextS);
			original.remove(nextS);
			p=nextS.getPosition();
		}
		return sorted;
	}
	
	/**
	 * Make a move. Go to the next station with positive coin and power,
	 * use method waste() to make moves after visiting all stations with positive coin and power.
	 * @return true if the power is not negative; false otherwise.
	 */
	private boolean next() {
		if(path.size()==0) {
			// if drone has visited all stations with the positive coin and power
			waste();
			return false;
		}else {
			Station target = path.get(0);
			if (target.distance(currentPosition)<0.00025) {
				//if the distance between the drone and the station before moving is less than 0.00025
			    //in this case, it cannot collect from the target station because it must be more closer to another station
				randomStep();
			}else {
				if (tryTime<20) {
					astar(currentPosition,target);
				}else {
					//consider visiting the station with negative coins and power with the descending order of the coins
					for (int i=0;i<danger.size();i++) {
						if (-danger.get(i).getCoins()>target.getCoins()) {
							path.remove(target);
							break;
						}else {
							danger.get(i).setSymbol(Symbol.lighthouse);
							astar(currentPosition,target);
							if (danger.get(i).getCoins()<0 && danger.get(i).getPower()<0) {
								//if this change cannot make the drone reach the target station
								danger.get(i).setSymbol(Symbol.danger);
							}else {
								if (danger.get(i).getCoins()==0 && danger.get(i).getPower()==0) {
									path.remove(target);
								}
								break;
							}
							
						}
					}
				}
//				System.out.printf(" %d %s %s\n",step,currentPosition.toString(),target.toPosition().toString());
			}
		}
		return this.power>0;
	}

	/**
	 * Find a direction which is not within the dangerous station and in the play area,
	 * move with this direction and then return.
	 * Repeat these 2 steps until 250 moves or no more power.
	 */
	private void waste() {
		StringBuilder sb = new StringBuilder(); 
		Direction nextD = null;
		for (Direction d : Direction.values()) {
			Position p = currentPosition.nextPosition(d);
			if (p.inPlayArea()&&(nearestChargableStation(p)==null||!(nearestChargableStation(p).getSymbol()==Symbol.danger))) {
				nextD=d;
				break;
			}
		}
		while (setPower(-1.25f)==1.25f && step<250) {
			// repeat moving to the opposite direction with the previous move
			sb.append(currentPosition.latitude);
			sb.append(",");
			sb.append(currentPosition.longitude);
			sb.append(",");	
			step ++;
			currentPosition=currentPosition.nextPosition(nextD);
			nextD = Direction.opposite(nextD);
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
			points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		}
		output = output + sb.toString();
	}
	
	/**
	 * Check if the given position can charge from the given station.
	 * @param targetStation - the station which is charged
	 * @param p - the position to be checked
	 * @return true if given position can charge from the given station; false otherwise
	 */
	private boolean canCharge(Station targetStation, Position p) {
		if (targetStation.distance(p)>0.00025) {return false;}
		double d = targetStation.distance(p);
		for (Station s : stations) {
			if (s.distance(p)<d) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return an arraylist of direction which is valid(in the play area and cannot be discharged by nearby station) from specified position.
	 * @param p - a specified position
	 * @return an arraylist of direction which is valid from specified position
	 */
	private ArrayList<Direction> availableDirection(Position p) {
		ArrayList<Direction> availableDirection = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			Position nextP = p.nextPosition(d);
			if (nextP.inPlayArea()&&(nearestChargableStation(nextP)==null||!(nearestChargableStation(nextP).getSymbol()==Symbol.danger))) {
				availableDirection.add(d);
			}
		}
		return availableDirection;
	}
	
	/**
	 * Take a random move to a position which is in the play area and cannot collect negative power and coin
	 */
	private void randomStep() {
		StringBuilder sb = new StringBuilder(); 
		if (setPower(-1.25f)!=1.25f|| step>=250) {
			return;
		}
		sb.append(currentPosition.latitude);
		sb.append(",");
		sb.append(currentPosition.longitude);
		sb.append(",");	
		step ++;
		ArrayList<Direction> availableDirections=availableDirection(currentPosition);
		Direction nextD=availableDirections.get(rnd.nextInt(availableDirections.size()));

		currentPosition=currentPosition.nextPosition(nextD);
		Station nearestStation = nearestChargableStation(currentPosition);
		
		if (nearestStation!=null) {
			nearestStation.update(setCoins(nearestStation.getCoins()),setPower(nearestStation.getPower()));
			path.remove(nearestStation);
		}

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
		points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		output = output + sb.toString();
	}

	/**
	 * Find a path to from specified position to the specified target station with the a star algorithm(from wikipedia).
	 * @param start - the start position
	 * @param target - the target station
	 */
	private void astar(Position start, Station target) {
		Position goal = target.getPosition();
		// For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start to n currently known.
		HashMap<Position, Position> cameFrom = new HashMap<Position, Position>();
		HashMap<Position, Direction> cameDirection = new HashMap<Position, Direction>();
		
		// For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
		HashMap<Position, Double> gScore = new HashMap<Position, Double>();
		gScore.put(start, 0.0);
		
		// For node n, fScore[n] := gScore[n] + h(n).
		HashMap<Position, Double> fScore = new HashMap<Position, Double>();
		fScore.put(start, heuristic(start,goal));
		
		Comparator<Position> c=new Comparator<Position>()  {
			@Override
			public int compare(Position o1, Position o2) {
				double d = (fScore.get(o1)-fScore.get(o2));
				if (d>0){
					return 1;
				}else if(d==0) {
					return 0;
				}else {
					return -1;
				}
			}
		};
		 // The set of discovered nodes that may need to be (re-)expanded.
		PriorityQueue<Position> openSet = new PriorityQueue<Position>(c);
		openSet.add(start);
		
		while (openSet.size()>0) {
			if (openSet.size()>10000) {
				randomStep();
				tryTime++;
				return;
			}
			Position current = null;
			current=openSet.poll();
			if (current==null) {
				continue;
			}

			if (nearestChargableStation(current)==target) {//in range
				path.remove(target);
				reconstruct_path(cameFrom, cameDirection, current, target.getId());
				tryTime=0;
				return;
			}
			
			for (Direction d : availableDirection(current)) {
				Position neighbor = current.nextPosition(d);
				// tentative_gScore is the distance from start to the neighbor through current
				double tentative_gScore=gScore.get(current)+0.0003;
				if(!gScore.containsKey(neighbor)) {
					gScore.put(neighbor, 100.0);
				}
				if (tentative_gScore<gScore.get(neighbor)) {
					// This path to neighbor is better than any previous one.
					cameFrom.put(neighbor, current);
					cameDirection.put(neighbor, d);
					gScore.put(neighbor, tentative_gScore);
					fScore.put(neighbor, gScore.get(neighbor)+heuristic(neighbor,goal));
					if(!isExplored(openSet,neighbor)){
						openSet.add(neighbor);
					}
				}
			}
		}
	}
	
	/**
	 * Reconstruct the path with the cameFrom hashmap created by the method astar
	 * @param cameFrom - A hashmap which record the position immediately preceding it on the cheapest path
	 * @param current - the start position
	 * @param targetID - the ID of the target station
	 */
	private void reconstruct_path(HashMap<Position, Position> cameFrom, HashMap<Position, Direction> cameDirection, Position current, String targetID) {
		ArrayList<Position> path = new ArrayList<Position>();
		ArrayList<Direction> directionPath = new ArrayList<Direction>();
		path.add(current);
		
		while (cameFrom.keySet().contains(current)) {
			current=cameFrom.get(current);
			path.add(current);
			directionPath.add(cameDirection.get(current));
		}
		Collections.reverse(path);
		Collections.reverse(directionPath);
		StringBuilder sb = new StringBuilder();
		for(int i = 1;i<path.size();i++) {
			if (setPower(-1.25f)!=1.25f|| step>=250) {
				output = output + sb.toString();
				return;
			}
			sb.append(path.get(i-1).latitude);
			sb.append(",");
			sb.append(path.get(i-1).longitude);
			sb.append(",");	
			step ++;
			currentPosition=path.get(i);
			// check if we pass the other station that we can charge
			for (Station s : stations) {
				if (canCharge(s,currentPosition)) {
					s.update(setCoins(s.getCoins()),setPower(s.getPower()));
					break;
				}
			}
			sb.append(directionPath.get(i-1));
			sb.append(",");
			sb.append(path.get(i).latitude);
			sb.append(",");
			sb.append(path.get(i).longitude);
			sb.append(",");
			sb.append(coins);
			sb.append(",");
			sb.append(power);
			sb.append("\n");
			points.add(Point.fromLngLat(path.get(i).longitude, path.get(i).latitude));
		}
		output = output + sb.toString();
	}
	
	/**
	 * Heuristic function for the a star algorithm.
	 * @param p - the current position in the a star algorithm
	 * @param goal - the goal position in the a star algorithm
	 * @return the heuristic value at the specific position
	 */
	private double heuristic(Position p, Position goal) {
		return goal.distance(p);
	}
	
	/**
	 * Check if a specified position is in an arraylist of position.
	 * @param openSet - an arraylist of position
	 * @param p - a position to be checked
	 * @return true if the specified position is in an arraylist of position; false otherwise
	 */
	private boolean isExplored(PriorityQueue<Position> openSet, Position p) {
		for (Position s : openSet) {
			// use Double.compare rather than == for the double
			if (Double.compare(s.latitude, p.latitude)==0 &&
					Double.compare(s.longitude, p.longitude)==0) {
				return true;
			}
		}
		return false;
	}
	
	

}