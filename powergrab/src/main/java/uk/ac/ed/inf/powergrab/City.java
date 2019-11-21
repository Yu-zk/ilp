package uk.ac.ed.inf.powergrab;


public class City {
	double x;
	double y;

	// 生成一个随机的城市
	public City(){
		this.x = (Math.random()*200);
		this.y = (Math.random()*200);
	}

	public City(double d, double e){
		this.x = d;
		this.y = e;
	}

	public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	// 计算两个城市之间的距离
	public double distanceTo(City city){
		double xDistance = Math.abs(getX() - city.getX());
		double yDistance = Math.abs(getY() - city.getY());
		double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );

		return distance;
	}

	@Override
	public String toString(){
		return getX()+", "+getY();
	}
}