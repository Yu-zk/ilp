package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import com.mapbox.geojson.Point;

public class Stateful extends Drone{

	private Direction nextD=Direction.N;
	private ArrayList<Station> path;
	private ArrayList<Station> lighthouse;
	private ArrayList<Station> danger = new ArrayList<Station>();
	private int tryTime = 0;

	/**
     * Constructor to create a new statefull drone instance by the constructor of superclass.
     * Initialise the list path which contains all stations with the symbol lighthouse,
     *   then create a path by the greedy algorithm.
     * Initialise the list lighthouse which contains all stations with the symbol lighthouse.
     * Initialise the list danger which contains all stations with the symbol danger,
     *   then sort it with the descending order of coins.
     * @param latitude - the latitude of this Drone
     * @param longitude - the longitude of this Drone
     * @param seed - the random seed
     * @param stations - the list of all stations
     */
	public Stateful(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, seed, stations);
		points.add(Point.fromLngLat(longitude, latitude));
		path = new ArrayList<Station>();
		for (Station s:stations) {
			if (Symbol.lighthouse==s.getSymbol()) {
				path.add(s);
			}
		}
		path = findPath(path);
		lighthouse = new ArrayList<Station>(path);
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
	}
	
	/**
	 * Run the stateful simulator. Make a move, if the number of move is less than 250 and power is positive.
	 */
	public void run() {
		while(step < 250 && next()) {}
//		System.out.printf("%f %f ",coins,power);
	}
	
	/**
	 * Build a path with the shortest total distance by the greedy algorithm
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
			waste();
			return false;
		}else {
			Station target = path.get(0);
			
			if (target.distance(currentPosition)<0.00025) {
				randomStep();
			}else {
				if (tryTime<20) {
					astar(currentPosition,target);
				}else {//change redpoint one by one
					for (int i=0;i<danger.size();i++) {
						if (-danger.get(i).getCoins()>target.getCoins()) {
							path.remove(target);
							break;
						}else {
							danger.get(i).setSymbol(Symbol.lighthouse);
							astar(currentPosition,target);
							danger.get(i).setSymbol(Symbol.danger);
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
		for (Direction d : Direction.values()) {
			Position p = currentPosition.nextPosition(d);
			if (p.inPlayArea()&&(nearestStation(p)==null||!(nearestStation(p).getSymbol()==Symbol.danger))) {
				nextD=d;
				break;
			}
		}
		while (setPower(-1.25)==1.25 && step<250) {
			sb.append(currentPosition.latitude);sb.append(",");
			sb.append(currentPosition.longitude);sb.append(",");	
			step ++;
			currentPosition=currentPosition.nextPosition(nextD);
			nextD = opposite(nextD);
			sb.append(nextD);sb.append(",");
			sb.append(currentPosition.latitude);sb.append(",");
			sb.append(currentPosition.longitude);sb.append(",");
			sb.append(coins);sb.append(",");
			sb.append(power);sb.append("\n");
			points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		}
		out = out + sb.toString();
	}
	
	/**
	 * Check if the given position can charge from the given station
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
	 * Return an arraylist of direction which is valid(in the play area and cannot be discharged by nearby station) from specified position
	 * @param p - a specified position
	 * @return an arraylist of direction which is valid from specified position
	 */
	private ArrayList<Direction> availableDirection(Position p) {
		ArrayList<Direction> availableDirection = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			Position nextP = p.nextPosition(d);
			if (nextP.inPlayArea()&&(nearestStation(nextP)==null||!(nearestStation(nextP).getSymbol()==Symbol.danger))) {
				availableDirection.add(d);
			}
		}
		return availableDirection;
	}
	
	/**
	 * Return the station can be collected from the specified position, null if there is no station
	 * @param p - a specified position
	 * @return the station can be collected from the specified position
	 */
	private Station nearestStation(Position p) {
		double min = 0.00025;
		Station nearestStation= null;
		for (Station s: stations) {
			double d=s.distance(p);
			if (d<min) {
				min = d;
				nearestStation = s;
			}
		}
		return nearestStation;
	}
	
	/**
	 * Take a random move to a position which is in the play area and cannot collect negative power and coin
	 */
	private void randomStep() {
		StringBuilder sb = new StringBuilder(); 
		if (setPower(-1.25)!=1.25|| step>=250) {
			return;
		}
		sb.append(currentPosition.latitude);sb.append(",");
		sb.append(currentPosition.longitude);sb.append(",");	
		step ++;
		ArrayList<Direction> availableDirections=availableDirection(currentPosition);
		nextD=availableDirections.get(rnd.nextInt(availableDirections.size()));

		currentPosition=currentPosition.nextPosition(nextD);
		Station nearestStation = nearestStation(currentPosition);
		
		if (nearestStation!=null) {
			nearestStation.update(setCoins(nearestStation.getCoins()),setPower(nearestStation.getPower()));
			path.remove(nearestStation);
		}

		sb.append(nextD);sb.append(",");
		sb.append(currentPosition.latitude);sb.append(",");
		sb.append(currentPosition.longitude);sb.append(",");
		sb.append(coins);sb.append(",");
		sb.append(power);sb.append("\n");
		points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		out = out + sb.toString();
	}
	
	/**
	 * Reconstruct the path with the cameFrom hashmap created by the method astar
	 * @param cameFrom - A hashmap which record the position immediately preceding it on the cheapest path
	 * @param current - the start position
	 * @param targetID - the ID of the target station
	 */
	private void reconstruct_path(HashMap<Position, Position> cameFrom, Position current, String targetID) {
		ArrayList<Position> path = new ArrayList<Position>();
		path.add(current);
		while (cameFrom.keySet().contains(current)) {
			current=cameFrom.get(current);
			path.add(current);
		}
		Collections.reverse(path);
		StringBuilder sb = new StringBuilder(); //check length <30
		for(int i = 1;i<path.size();i++) {
			if (setPower(-1.25)!=1.25|| step>=250) {
				out = out + sb.toString();
				return;
			}
			sb.append(path.get(i-1).latitude);sb.append(",");
			sb.append(path.get(i-1).longitude);sb.append(",");	
			step ++;
			currentPosition=path.get(i);
			for (Station s : stations) {
				if (canCharge(s,currentPosition)) {
					s.update(setCoins(s.getCoins()),setPower(s.getPower()));
					break;
				}
			}
			sb.append(nextD);sb.append(",");
			sb.append(path.get(i).latitude);sb.append(",");
			sb.append(path.get(i).longitude);sb.append(",");
			
			if(i==path.size()-1) {
				for (Station s : lighthouse) {
					if (targetID.equals(s.getId())) {
						s.update(setCoins(s.getCoins()),setPower(s.getPower()));
						break;
					}
				}
			}
			sb.append(coins);sb.append(",");
			sb.append(power);sb.append("\n");
			points.add(Point.fromLngLat(path.get(i).longitude, path.get(i).latitude));
		}
		out = out + sb.toString();
	}
	
	/**
	 * Find a path to from specified position to the specified target station with the a star algorithm(from wikipedia)
	 * @param start - the start position
	 * @param target - the target station
	 */
	public void astar(Position start, Station target) {
		Position goal = target.getPosition();
		ArrayList<Position> openSet = new ArrayList<Position>();
		openSet.add(start);
		HashMap<Position, Position> cameFrom = new HashMap<Position, Position>();
		HashMap<Position, Double> gScore = new HashMap<Position, Double>();
		gScore.put(start, 0.0);
		HashMap<Position, Double> fScore = new HashMap<Position, Double>();
		fScore.put(start, h(start,goal));
		while (openSet.size()>0) {
			if (openSet.size()>8000) {
				randomStep();
				tryTime++;
				return;
			}
			Position current = null;
			double min=100;
			for (Position p:openSet) {
				if (fScore.get(p)<min && fScore.get(p)<0.01+target.distance(start)) {
					current=p;
					min=fScore.get(p);
				}
			}
			if (min==100) {continue;}

			if (goal.distance(current)<0.00025) {//in range
				path.remove(target);
				reconstruct_path(cameFrom,current,target.getId());
				if (tryTime>1) {
					System.out.printf("%d ",tryTime);
				}
				
				tryTime=0;
				return;
			}
			openSet.remove(current);
			for (Direction d : availableDirection(current)) {
				Position neighbor = current.nextPosition(d);//TODO check if pass other 
				double tentative_gScore=gScore.get(current)+0.0003;
				if(!gScore.containsKey(neighbor)) {
					gScore.put(neighbor, 100.0);
				}
				if (tentative_gScore<gScore.get(neighbor)) {
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, tentative_gScore);
					fScore.put(neighbor, gScore.get(neighbor)+h(neighbor,goal));
					if(!isExplored(openSet,neighbor)){
						openSet.add(neighbor);
					}
				}
			}
		}
	}
	
	/**
	 * Heuristic function for the a star algorithm.
	 * @param p - the current position in the a star algorithm
	 * @param goal - the goal position in the a star algorithm
	 * @return the heuristic value at the specific position
	 */
	private double h(Position p, Position goal) {
		return goal.distance(p);
	}
	
	/**
	 * Check if a specified position is in an arraylist of position
	 * @param openSet - an arraylist of position
	 * @param p - a position to be checked
	 * @return true if the specified position is in an arraylist of position; false otherwise
	 */
	private boolean isExplored(ArrayList<Position> openSet, Position p) {
		for (Position s : openSet) {
			if (Double.compare(s.latitude, p.latitude)==0 &&
					Double.compare(s.longitude, p.longitude)==0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the opposite direction with the given direction
	 * @param d - a direction
	 * @return the opposite direction with the given direction
	 */
	private Direction opposite(Direction d) {
		if (d==null) {
			return null;
		}
		switch (d) {
		case E:
			return (Direction.W);
		case ENE:
			return (Direction.WSW);
		case NE:
			return (Direction.SW);
		case NNE:
			return (Direction.SSW);
		case N:
			return (Direction.S);
		case NNW:
			return (Direction.SSE);
		case NW:
			return (Direction.SE);
		case WNW:
			return (Direction.ESE);
		case W:
			return (Direction.E);
		case WSW:
			return (Direction.ENE);
		case SW:
			return (Direction.NE);
		case SSW:
			return (Direction.NNE);
		case S:
			return (Direction.N);
		case SSE:
			return (Direction.E);
		case SE:
			return (Direction.NW);
		case ESE:
			return (Direction.WNW);
		default:
			throw new IllegalArgumentException("Direction does not exist.");
		}
	}

}