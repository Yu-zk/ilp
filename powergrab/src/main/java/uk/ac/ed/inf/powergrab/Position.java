package uk.ac.ed.inf.powergrab;

/**
 * A class to represent a position on the map.
 * Each position is identified by a latitude and a longitude.
 *
 */
public class Position {
	public double latitude;
	public double longitude;
	// Use static to ensure these values are only calculated once.
	// This can avoid duplicated calculation in every movement.
	public static final double sin45 = 0.0003 * Math.sin(Math.PI / 4);
	public static final double sin225 = 0.0003 * Math.sin(Math.PI / 8);
	public static final double cos225 = 0.0003 * Math.cos(Math.PI / 8);
		
	/**
	 * Create a new position and sets the location of this position to the specified double latitude and longitude.
	 * @param latitude - the latitude of the position
     * @param longitude - the longitude of the position
	 */
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Calculate the final position from the current position with a given direction.
	 * The position itself will not be changed.
	 * @param direction - The direction it wants to go.
	 * @return the next position after moving toward the direction specified
	 */
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
	
	/**
	 * Determines whether or not this position is in the specific area.
	 * @return true if this position is in the specific area; false otherwise.
	 */
	public boolean inPlayArea() {
		// drone on the edge is invalid, so no equal in the condition.
		if (latitude > 55.942617 && latitude < 55.946233 
				&& longitude > -3.192473 && longitude < -3.184319) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the Euclidean distance from this position to a specified position.
	 * @param p - The specified position to be measured against this position
	 * @return the Euclidean distance between this position and a specified position.
	 */
	public double distance(Position p) {
		return Math.sqrt((latitude-p.latitude)*(latitude-p.latitude) + (longitude-p.longitude)*(longitude-p.longitude));
	}
	
	/**
	 * Override the toString method to make the output easier to read when debug.
	 */
	@Override
	public String toString() {
		return "Position (" + latitude + ", " + longitude + ")";
	}
	
}