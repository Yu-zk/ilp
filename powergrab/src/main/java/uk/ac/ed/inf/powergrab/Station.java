package uk.ac.ed.inf.powergrab;

public class Station {
	private String id;
    private double coins;
    private double power;
    private String symbol;
    private String color;
    private double latitude;
	private double longitude;
	public Station(String id, double coins, double power, String symbol, String color, double latitude,
			double longitude) {
		super();
		this.id = id;
		this.coins = coins;
		this.power = power;
		this.symbol = symbol;
		this.color = color;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public void update(double coins, double power) {
		this.coins = this.coins + coins;
		this.power = this.power + power;
		if (this.coins == 0 && this.power == 0) {
			this.symbol = "zero";
		}
	}
	public double distance(Position p) {
		return Math.sqrt((latitude-p.latitude)*(latitude-p.latitude) + (longitude-p.longitude)*(longitude-p.longitude));
	}
	public Position toPosition() {
		return new Position(latitude, longitude);
	}
	
	public Position getPosition() {
		return new Position(latitude, longitude);
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
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
	public double getCoins() {
		return coins;
	}
	public void setCoins(double coins) {
		this.coins = coins;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
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

	


	@Override
	public String toString() {
		return "Station [id=" + id + ", coins=" + coins + ", power=" + power + ", symbol=" + symbol + ", color=" + color
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]\n";
	}
	
    

    


}
