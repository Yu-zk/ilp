package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.mapbox.geojson.Point;

public class Stateful2 extends Drone{

	private Direction nextD=Direction.N;


	public Stateful2(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, stations, seed);
		points.add(Point.fromLngLat(longitude, latitude));


	}
	public void run() {
		while(step < 250 && next()) {
			
		}
		System.out.printf("%f %f\n",coins,power);
	}
	private boolean next() {
		double min = 1;
		Station target= null;

		for (Station s: stations) {
			double d=s.distance(currentPosition);

			if ("lighthouse".equals(s.getSymbol()) && d<min) {
				min = d;
				target = s;
			}
		}
	
		if(target==null) {
			System.out.println("finish");
			StringBuilder sb = new StringBuilder(); //out of while loop
			while (setPower(-1.25)==1.25 && step<250) {
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");	
				step ++;
				nextD = opposite(nextD);
				currentPosition=currentPosition.nextPosition(nextD);
				sb.append(nextD);sb.append(",");
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");
				sb.append(coins);sb.append(",");
				sb.append(power);sb.append("\n");
				points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
				//System.out.print(sb);
				out = out + sb.toString();
			}
			return false;
		}else {
			if(step==9) {
				min=10;
			}
			if (target.distance(currentPosition)<0.00025) {
				StringBuilder sb = new StringBuilder(); 
				if (setPower(-1.25)!=1.25|| step>=250) {
					out = out + sb.toString();
					return false;
				}
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");	
				step ++;
				min=10;
				nextD=null;
				for (Direction d : Direction.values()) {
					Position nextP = currentPosition.nextPosition(d);
//					double distance = target.distance(nextP);
//					boolean isCloestStation=true;
//					for (Station s : stations) {
//						if (s.distance(nextP)<distance) {
//							isCloestStation=false;
//							break;
//						}
//					}
//					if (!isCloestStation) {continue;}
					if (target.distance(nextP)<min&&(!isHarmful(nextP))&&nextP.inPlayArea()) {
						
						min=target.distance(nextP);
						nextD=d;
					}
				}
				currentPosition=currentPosition.nextPosition(nextD);
				System.out.print(nextD);

				for (Station s : stations) {
					if (s.distance(currentPosition)<0.00025) {
						s.update(setCoins(s.getCoins()),setPower(s.getPower()));
						break;
					}
				}
				
				sb.append(nextD);sb.append(",");
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");
				
//				if(target.distance(currentPosition)<0.00025) {
//					target.update(setCoins(target.getCoins()),setPower(target.getPower()));
//				}
				sb.append(coins);sb.append(",");
				sb.append(power);sb.append("\n");
				points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
				out = out + sb.toString();
			}else {
				astar(currentPosition,target);
				System.out.printf("%d %s %s\n",step,currentPosition.toString(),target.toPosition().toString());
			}
			

		}

		return this.power>0;
	}


	private Direction nextDirection(Station s) {
		double min = 1;
		Direction nextD = null;
		Direction nextDWithNeg = null;
		for (Direction d : Direction.values()) {
			Position nextPosition = currentPosition.nextPosition(d);
			double distance = s.distance(nextPosition);
			if (distance < min && nextPosition.inPlayArea()) {
				nextDWithNeg = d;
				if (!isHarmful(nextPosition)) {
					min = distance;
					nextD = d;
				}
			}
		}
		if (nextD==null) {
			return nextDWithNeg;
		}
		return nextD;
	}
	private boolean isHarmful(Position p) {
		double min = 0.00025;
		Station nearestStation= null;
		for (Station s: stations) {
			double d=s.distance(p);
			if (d<min) {
				min = d;
				nearestStation = s;
			}
		}
		if (nearestStation==null) {
			return false;
		}
		return "danger".equals(nearestStation.getSymbol());
	}

	private Direction otherDirection(Direction d) {
		Direction nextD = Direction.values()[rnd.nextInt(16)];//||d==nextD
		while ((!currentPosition.nextPosition(nextD).inPlayArea()) || isHarmful(currentPosition.nextPosition(nextD))) {
			nextD = Direction.values()[rnd.nextInt(16)];
		}
		return nextD;
	}
	private boolean inRange(Station targetStation, Position p) {
		if (targetStation.distance(p)>0.00025) {return false;}
		double d = targetStation.distance(p);
		for (Station s : stations) {
			if (s.distance(p)<d) {
				return false;
			}
		}
		return true;
	}
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


	private double h(Position p, Position goal) {

//		return Math.sqrt((p.longitude-goal.longitude)*(p.longitude-goal.longitude) + 
//				(p.latitude-goal.latitude)*(p.latitude-goal.latitude));
		return goal.distance(p);

	}
	private ArrayList<Direction> availableDirection(Position s) {
		ArrayList<Direction> availableDirection = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			Position nextP = s.nextPosition(d);
			if (!isHarmful(nextP)&&nextP.inPlayArea()) {
				availableDirection.add(d);
			}
		}
		return availableDirection;
	}
	
	private void path(HashMap<Position, Position> cameFrom,Position current,String targetID) {
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
			
			//move
			
			currentPosition=path.get(i);
			for (Station s : stations) {
				if (inRange(s,currentPosition)) {
					s.update(setCoins(s.getCoins()),setPower(s.getPower()));
					break;
				}
			}
			
			
			sb.append(nextD);sb.append(",");
			sb.append(path.get(i).latitude);sb.append(",");
			sb.append(path.get(i).longitude);sb.append(",");
			
			if(i==path.size()-1) {
				for (Station s : stations) {
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


	
	public void astar(Position start, Station target) {
		Position goal = target.toPosition();
		ArrayList<Position> openSet = new ArrayList<Position>();
		openSet.add(start);

		HashMap<Position, Position> cameFrom = new HashMap<Position, Position>();

		HashMap<Position, Double> gScore = new HashMap<Position, Double>();
		gScore.put(start, 0.0);

		HashMap<Position, Double> fScore = new HashMap<Position, Double>();
		fScore.put(start, h(start,goal));

		while (openSet.size()>0) {
			Position current = null;
			double min=100;
			for (Position p:openSet) {
				if (fScore.get(p)<min && fScore.get(p)<10+target.distance(start)) {
					current=p;
					min=fScore.get(p);
				}
			}

			if (goal.distance(current)<0.00025) {//in range
				//reconstruct_path(cameFrom, current)
				path(cameFrom,current,target.getId());
				return;
			}
//			System.out.println(openSet.size());

			openSet.remove(current);

			//			for each neighbor of current
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
					//System.out.println(gScore.get(neighbor)+h(neighbor,goal));

//					if(!openSet.contains(neighbor)){
//						openSet.add(neighbor);
//					}
					if(!isExplored(openSet,neighbor)){
						openSet.add(neighbor);
					}
					

				}
			}


		}
	}
	private boolean isExplored(ArrayList<Position> openSet, Position p) {
		for (Position s : openSet) {
			if (Double.compare(s.latitude, p.latitude)==0 &&
					Double.compare(s.longitude, p.longitude)==0) {
				return true;
			}
		}
		return false;
	}

}
