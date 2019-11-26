package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.mapbox.geojson.Point;

public class Stateful extends Drone{

	private Direction nextD=Direction.N;
	private Direction[] directionList= new Direction[4];

	public Stateful(double latitude, double longitude, int seed, ArrayList<Station> stations) {
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

}
