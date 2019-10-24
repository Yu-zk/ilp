package uk.ac.ed.inf.powergrab;

public class Station {
    public String id;
    public double coins;
    public double power;
    private String symbol;
    private String color;
    public double latitude;
    public double longitude;
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
	


	@Override
	public String toString() {
		return "Station [id=" + id + ", coins=" + coins + ", power=" + power + ", symbol=" + symbol + ", color=" + color
				+ ", latitude=" + latitude + ", longitude=" + longitude + "]\n";
	}
	
    

    


}
