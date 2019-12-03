package uk.ac.ed.inf.powergrab;

/**
 * A enum class to represent all 16 possible directions.
 *
 */
public enum Direction {
    
	/**
	 * Enumerate all 16 possible directions.
	 *
	 */
	N, NNE, NE, ENE, 
	E, ESE, SE, SSE, 
	S, SSW, SW, WSW, 
	W, WNW, NW, NNW;
	

	/**
	 * Return the opposite direction with the given direction
	 * @param d - a direction
	 * @return the opposite direction with the given direction
	 */
	public static Direction opposite(Direction d) {
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
