package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude=longitude;
	}
	public Position nextPosition(Direction direction) { 
		int unit = 0;
		//each unit is 22.5degree, from East, consider a unit circle
		//in this way, positive and negative are included in sin and cos function
		//for example, WSW is 9*22.5=202.5, sin(202.5)=-0.38, cos(202.5)=-0.92
		//so the change of position is -0.38*0.0003 and -0.92*0.0003 
		switch (direction) {
		    case E:
		    	unit=0;
		    	break;
		    case ENE:
		    	unit=1;
		    	break;
		    case NE:
		    	unit=2;
		    	break;
		    case NNE:
		    	unit=3;
		    	break;
		    case N:
		    	unit=4;
		    	break;
		    case NNW:
		    	unit=5;
		    	break;
		    case NW:
		    	unit=6;
		    	break;
		    case WNW:
		    	unit=7;
		    	break;
		    case W:
		    	unit=8;
		    	break;
		    case WSW:
		    	unit=9;
		    	break;
		    case SW:
		    	unit=10;
		    	break;
		    case SSW:
		    	unit=11;
		    	break;
		    case S:
		    	unit=12;
		    	break;
		    case SSE:
		    	unit=13;
		    	break;
		    case SE:
		    	unit=14;
		    	break;
		    case ESE:
		    	unit=15;
		    	break;
		    default:
		    	throw new IllegalArgumentException("Direction does not exist.");
		}
		return (new Position(latitude+0.0003*Math.sin(Math.PI/8*unit), longitude+0.0003*Math.cos(Math.PI/8*unit)));
	}
	public boolean inPlayArea() {
		//on the edge is invalid, so no equal in the condition
		if (latitude>55.942617 && latitude<55.946233 
				&& longitude>-3.192473 && longitude<-3.184319) {
			return true;
		}
		return false;
	}
}