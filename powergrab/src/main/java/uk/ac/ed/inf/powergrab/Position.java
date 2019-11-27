package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	// use static to ensure these values are only calculated once.
	// this can avoid duplicated calculation in every movement.
	public static final double sin45 = 0.0003 * Math.sin(Math.PI / 4);
	public static final double sin225 = 0.0003 * Math.sin(Math.PI / 8);
	public static final double cos225 = 0.0003 * Math.cos(Math.PI / 8);
	
	
	public Position nextPosition(Direction direction) { 
		// use switch to check which direction it goes and return the final position with the value calculated before.
		switch (direction) {
		    case E:
		    	return (new Position(latitude, longitude + 0.0003));
		    case ENE:
		    	return (new Position(latitude + sin225, longitude + cos225));
		    case NE:
		    	return (new Position(latitude + sin45, longitude + sin45));
		    case NNE:
		    	return (new Position(latitude + cos225, longitude + sin225));
		    case N:
		    	return (new Position(latitude + 0.0003, longitude));
		    case NNW:
		    	return (new Position(latitude + cos225, longitude - sin225));
		    case NW:
		    	return (new Position(latitude + sin45, longitude - sin45));
		    case WNW:
		    	return (new Position(latitude + sin225, longitude - cos225));
		    case W:
		    	return (new Position(latitude, longitude - 0.0003));
		    case WSW:
		    	return (new Position(latitude - sin225, longitude - cos225));
		    case SW:
		    	return (new Position(latitude - sin45, longitude - sin45));
		    case SSW:
		    	return (new Position(latitude - cos225, longitude - sin225));
		    case S:
		    	return (new Position(latitude - 0.0003, longitude));
		    case SSE:
		    	return (new Position(latitude - cos225, longitude + sin225));
		    case SE:
		    	return (new Position(latitude - sin45, longitude + sin45));
		    case ESE:
		    	return (new Position(latitude - sin225, longitude + cos225));
		    default:
		    	throw new IllegalArgumentException("Direction does not exist.");
		}
		
	}
	public boolean inPlayArea() {
		// drone on the edge is invalid, so no equal in the condition.
		if (latitude > 55.942617 && latitude < 55.946233 
				&& longitude > -3.192473 && longitude < -3.184319) {
			return true;
		}
		return false;
	}
	
	public boolean equals(Position p) {
		return p.latitude==latitude && p.longitude==longitude;
	}
	public double distance(Position p) {
		return Math.sqrt((latitude-p.latitude)*(latitude-p.latitude) + (longitude-p.longitude)*(longitude-p.longitude));
	}
	
	
	
}