package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.mapbox.geojson.Point;

public class StatefulOld extends Drone{

	private Direction nextD=Direction.N;
	private Direction[] directionList= new Direction[4];

	public StatefulOld(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, stations, seed);
		points.add(Point.fromLngLat(longitude, latitude));


	}
	public void run() {
		while(step < 250 && next(new ArrayList<String>())) {
			step++;
		}
		System.out.printf("%f %f\n",coins,power);
	}
	private boolean next(List<String> id) {

		double min = 1;
		Station target= null;

		for (Station s: stations) {
			double d=s.distance(currentPosition);

			id.contains(s.getId());
			if ((!id.contains(s.getId()))&&("lighthouse".equals(s.getSymbol())) && d<min) {
				min = d;
				target = s;
			}
		}
		if(target==null) {
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
				//				System.out.print(sb);
				out = out + sb.toString();
			}
			return false;
		}else {
			boolean arrived = false;  //   inRange(target);
			//			TODO start with green

			StringBuilder sb = new StringBuilder(); 
			int times=0;

			while (step < 250 && !arrived&&times<31) {
				if (setPower(-1.25)!=1.25|| step>=250) {
					return false;
				}
				times++;
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");	
				step ++;
				nextD = nextDirection(target);

				directionList[3]=directionList[2];
				directionList[2]=directionList[1];
				directionList[1]=directionList[0];
				directionList[0]=nextD;
				if (directionList[0]==opposite(directionList[3])) {//&&directionList[0]==directionList[2]  20-06-2019
					nextD=otherDirection(nextD);
					directionList[0]=nextD;
				}
				System.out.println(target.getId());
				System.out.println(step);
				if (step==219) {
					//						step=1000000;
					System.out.print(1);
				}
				currentPosition=currentPosition.nextPosition(nextD);
				sb.append(nextD);sb.append(",");
				sb.append(currentPosition.latitude);sb.append(",");
				sb.append(currentPosition.longitude);sb.append(",");
				// can save the index before
				arrived = inRange(target);
				if (arrived) {
					for (Station s : stations) {
						if (s.getId()==target.getId()) {
							s.update(setCoins(s.getCoins()),setPower(s.getPower()));
							break;
						}
					}
				}
				sb.append(coins);sb.append(",");
				sb.append(power);sb.append("\n");

				points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
			}
			if(times==31) {
				id.add(target.getId());
				next(id);
			}
			//							System.out.print(sb);
			out = out + sb.toString();
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
				if (!isHarful(nextPosition)) {
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
	private boolean isHarful(Position p) {
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
		while ((!currentPosition.nextPosition(nextD).inPlayArea()) || isHarful(currentPosition.nextPosition(nextD))) {
			nextD = Direction.values()[rnd.nextInt(16)];
		}
		return nextD;
	}
	private boolean inRange(Station tragetStation) {
		if (tragetStation.distance(currentPosition)>0.00025) {return false;}
		double d = tragetStation.distance(currentPosition);
		for (Station s : stations) {
			if (s.distance(currentPosition)<d) {
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



	//	public void p() {
	//		int i=0;
	//		for (Station s : stations) {
	//			if (! "danger".equals(s.getSymbol())) {
	////				allCitys.add(new City(s.getLatitude(), s.getLongitude()));
	//				System.out.printf("%d %f %f\n",i,s.getLatitude(), s.getLongitude());
	//				i++;
	//			}
	//		}
	////		points.addAll(SimulatedAnnealing.run(stations));
	//	}
	private double h(Position p, Position goal) {

//		return Math.sqrt((p.longitude-goal.longitude)*(p.longitude-goal.longitude) + 
//				(p.latitude-goal.latitude)*(p.latitude-goal.latitude));
		return goal.distance(p);

	}
	private ArrayList<Direction> availableDirection(Position s) {
		ArrayList<Direction> availableDirection = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			if (!isHarful(s.nextPosition(d))) {
				availableDirection.add(d);
			}
		}
		return availableDirection;
	}
	
	private void path(HashMap<Position, Position> cameFrom,Position current) {
		ArrayList<Position> path = new ArrayList<Position>();
		path.add(current);
		while (cameFrom.keySet().contains(current)) {
			current=cameFrom.get(current);
			path.add(current);
		}
		Collections.reverse(path);
		for(Position p : path) {
			points.add(Point.fromLngLat(p.longitude, p.latitude));
		}
		
	}


	public void astar(Position start, Position goal) {
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
				if (fScore.get(p)<min) {
					current=p;
					min=fScore.get(p);
				}
			}
			if (goal.distance(current)<0.00025) {
				//reconstruct_path(cameFrom, current)
				path(cameFrom,current);
				return;
			}

			openSet.remove(current);

			//			for each neighbor of current
			for (Direction d : availableDirection(current)) {
				Position neighbor = current.nextPosition(d);
				
				if (goal.distance(neighbor)<0.00025) {
					//reconstruct_path(cameFrom, current)
//					cameFrom.put(neighbor, current);
					path(cameFrom,current);
					return;
				}
				
				double tentative_gScore=gScore.get(current)+0.0003;
				if(!gScore.containsKey(neighbor)) {
					gScore.put(neighbor, 100.0);
				}
				if (tentative_gScore<gScore.get(neighbor)) {
					cameFrom.put(neighbor, current);

					gScore.put(neighbor, tentative_gScore);
					fScore.put(neighbor, gScore.get(neighbor)+h(neighbor,goal));
					//System.out.println(gScore.get(neighbor)+h(neighbor,goal));

					if(!openSet.contains(neighbor)){
						openSet.add(neighbor);
					}
				}
			}


		}
	}

}
