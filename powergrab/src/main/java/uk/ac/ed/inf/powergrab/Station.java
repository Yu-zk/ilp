package uk.ac.ed.inf.powergrab;
/**
 * A class to represent a station on the map.
 * Each stations have latitude ,longitude, id, coin, power, symbol, color.
 * Coin, power and symbol can be changed when the drone passes it.
 */
public class Station {
	private String id;
	private double latitude;
	private double longitude;
    private float coins;
    private float power;
    private Symbol symbol;
    private String color;
    
	
	/**
	 * Create a new station and set the location of the station to specific double latitude ,longitude
	 * Also set the id, coin, power, symbol, color for the station.
	 *      * @param id
     * @param coins - the coin of the station
     * @param power - the power of the station
     * @param symbol - the symbol of the station
     * @param color - the color of the station
     * @param latitude - the latitude of the station
     * @param longitude - the longitude of the station
	 */
	public Station(String id, float coins, float power, Symbol symbol, String color, double latitude,
			double longitude) {
		this.id = id;
		this.coins = coins;
		this.power = power;
		this.symbol = symbol;
		this.color = color;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Update the information of the station.
	 * When a drone passes the station, the drone will collect coins and power.
	 * This method can reduce the coins and power collected by the drone.
	 * When both coins and power is zero, it changes the symbol zero.
	 * @param coins - coins that the drone collected
	 * @param power - power that the drone collected
	 */
	public void update(float coins, float power) {
		this.coins = this.coins + coins;
		this.power = this.power + power;
		if (this.coins == 0 && this.power == 0) {
			this.symbol = Symbol.zero;
		}
	}
	
	/**
	 * Returns the Euclidean distance from this station to a specified position.
	 * @param p - The specified position to be measured against this station.
	 * @return the Euclidean distance between this station and a specified position.
	 */
	public double distance(Position p) {
		return Math.sqrt((latitude-p.latitude)*(latitude-p.latitude) + (longitude-p.longitude)*(longitude-p.longitude));
	}
	
	/**
	 * Generate a position instance which has the same latitude and longitude with the station.
	 * @return Position which has the same latitude and longitude with the station.
	 */
	public Position getPosition() {
		return new Position(latitude, longitude);
	}
	
	/**
	 * Override the toString method to make the output easier to read when debug.
	 */
	@Override
	public String toString() {
		return "Station [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + ", coins=" + coins
				+ ", power=" + power + ", symbol=" + symbol + ", color=" + color + "]";
	}
	
	/**
	 * getter and setter for all private variable
	 */
	public Symbol getSymbol() {
		return symbol;
	}
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getCoins() {
		return coins;
	}
	public void setCoins(float coins) {
		this.coins = coins;
	}
	public float getPower() {
		return power;
	}
	public void setPower(float power) {
		this.power = power;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
