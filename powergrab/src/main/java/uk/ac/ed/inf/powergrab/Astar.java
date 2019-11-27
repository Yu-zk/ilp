package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Astar {
//	public static ArrayList<Position> reconstruct_path(cameFrom, current){
//
//	}
	private double h(Position p, Position goal) {
		
		return Math.sqrt((p.longitude-goal.longitude)*(p.longitude-goal.longitude) + 
				(p.latitude-goal.latitude)*(p.latitude-goal.latitude));
		
	}
	private Direction nextDirection(Station s,Position currentPosition) {
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
	private void astar(Position start, Position goal) {
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
			for (Position p:fScore.keySet()) {
				if (fScore.get(p)<min) {
					current=p;
					min=fScore.get(p);
				}
			}
			if (goal.equals(current)) {
//				reconstruct_path(cameFrom, current)
				return;
			}
			
			openSet.remove(current);
//			for each neighbor of current
			for (Position p : neighbour(current)) {
				
			}
//            // d(current,neighbor) is the weight of the edge from current to neighbor
//            // tentative_gScore is the distance from start to the neighbor through current
//            tentative_gScore := gScore[current] + d(current, neighbor)
//            if tentative_gScore < gScore[neighbor]
//                // This path to neighbor is better than any previous one. Record it!
//                cameFrom[neighbor] := current
//                gScore[neighbor] := tentative_gScore
//                fScore[neighbor] := gScore[neighbor] + h(neighbor)
//                if neighbor not in openSet
//                    openSet.add(neighbor)
			
		}
	}
	
	
	
	
//	public List<Position> aStarSearch(Position start, Position goal)
//	{
//
//		MapNode startNode = pointNodeMap.get(start);
//		MapNode endNode = pointNodeMap.get(goal);
//
//		// setup for A*
//		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
//		HashSet<MapNode> visited = new HashSet<MapNode>();
// 		Map<MapNode, Double> distances = initializeAllToInfinity();
//
//		Queue<MapNode> priorityQueue = initQueue();
//
//		//  enque StartNode, with distance 0
//		startNode.setDistanceToStart(new Double(0));
//		distances.put(startNode, new Double(0));
//		priorityQueue.add(startNode);
//		MapNode current = null;
//
//		while (!priorityQueue.isEmpty()) {
//			current = priorityQueue.remove();
//
//			if (!visited.contains(current) ){
//				visited.add(current);
//				// if last element in PQ reached
//				if (current.equals(endNode)) return reconstructPath(parentMap, startNode, endNode, 0);
//
//				Set<MapNode> neighbors = getNeighbors(current);
//				for (MapNode neighbor : neighbors) {
//					if (!visited.contains(neighbor) ){  
//
//						// calculate predicted distance to the end node
//						double predictedDistance = neighbor.getLocation().distance(endNode.getLocation());
//
//						// 1. calculate distance to neighbor. 2. calculate dist from start node
//						double neighborDistance = current.calculateDistance(neighbor);
//						double totalDistance = current.getDistanceToStart() + neighborDistance + predictedDistance;
//
//						// check if distance smaller
//						if(totalDistance < distances.get(neighbor) ){
//							// update n's distance
//							distances.put(neighbor, totalDistance);
//							// used for PriorityQueue
//							neighbor.setDistanceToStart(totalDistance);
//							neighbor.setPredictedDistance(predictedDistance);
//							// set parent
//							parentMap.put(neighbor, current);
//							// enqueue
//							priorityQueue.add(neighbor);
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}

}
