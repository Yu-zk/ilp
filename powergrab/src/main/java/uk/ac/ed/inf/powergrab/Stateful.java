package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import com.mapbox.geojson.Point;

public class Stateful extends Drone{

	private Direction nextD=Direction.N;
	private ArrayList<Station> lighthouse = new ArrayList<Station>();
	private ArrayList<Station> danger = new ArrayList<Station>();
	private int tryTime = 0;


	public Stateful(double latitude, double longitude, int seed, ArrayList<Station> stations) {
		super(latitude, longitude, stations, seed);
		points.add(Point.fromLngLat(longitude, latitude));
		for (Station s:stations) {
			if (Symbol.lighthouse==s.getSymbol()) {
				lighthouse.add(s);
			}
		}
		
		lighthouse = sort(lighthouse);
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
	private ArrayList<Station> sort(ArrayList<Station> original){
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
			p=nextS.toPosition();
//			points.add(Point.fromLngLat(p.longitude, p.latitude));
		}
		
		
		return sorted;
	}
	public void run() {
		while(step < 250 && next()) {
			
		}
//		System.out.printf("%f %f\n",coins,power);
	}
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
		ArrayList<Station> withinStation = new ArrayList<Station>();
		for (Station s : stations) {
			if (s.distance(currentPosition)<0.00025) {
				withinStation.add(s);
//				s.update(setCoins(s.getCoins()),setPower(s.getPower()));
//				break;
			}
		}
		double min=10;
		if (withinStation.size()>0) {
			min=10;
			Station nearestStation=withinStation.get(0);
			for (Station s:withinStation) {
				if(s.distance(currentPosition)<min) {
					min=s.distance(currentPosition);
					nearestStation=s;
				}
			}
			nearestStation.update(setCoins(nearestStation.getCoins()),setPower(nearestStation.getPower()));
			lighthouse.remove(nearestStation);
		}
		sb.append(nextD);sb.append(",");
		sb.append(currentPosition.latitude);sb.append(",");
		sb.append(currentPosition.longitude);sb.append(",");
		sb.append(coins);sb.append(",");
		sb.append(power);sb.append("\n");
		points.add(Point.fromLngLat(currentPosition.longitude, currentPosition.latitude));
		out = out + sb.toString();
	}
	private boolean next() {
		if(lighthouse.size()==0) {
//			System.out.println("finish");
			StringBuilder sb = new StringBuilder(); 
			for (Direction d : Direction.values()) {
				Position p = currentPosition.nextPosition(d);
				if (!isHarmful(p)&&p.inPlayArea()) {
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
				out = out + sb.toString();
			}
			return false;
		}else {
			Station target = lighthouse.get(0);
			
			if (target.distance(currentPosition)<0.00025) {
				randomStep();
				
			}else {
				if (tryTime<20) {
					astar(currentPosition,target);
				}else {//change redpoint one by one
					for (int i=0;i<danger.size();i++) {
						if (-danger.get(i).getCoins()>target.getCoins()) {
							lighthouse.remove(target);
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
		return Symbol.danger==nearestStation.getSymbol();
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
	private boolean arrive(Position current, Station target) {
		if (target.distance(current)>=0.00025) {return false;}
//		for (Station s : stations) {
//			if (s.distance(current)<0.00025&&!s.equals(target)) {
//				return false;
//			}
//		}
		return true;
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

			if (openSet.size()>8000) {//50000
//				target.setSymbol(Symbol.blackwhole);
//				System.out.printf("%d ",openSet.size());
//				System.out.print(lighthouse);
//				if (lighthouse.size()==1) {
//					astar(start, previousStation[0]);
//				}else {
				randomStep();
//				lighthouse.add(1,target);
//				}
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

//			if (goal.distance(current)<0.00025) {//in range
			if (arrive(current,target)) {//in range
				//reconstruct_path(cameFrom, current)
				tryTime=0;
				lighthouse.remove(target);
				path(cameFrom,current,target.getId());
				System.out.printf("%d ",openSet.size());
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