package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;

import com.mapbox.geojson.Point;

public class Tour{

	// 保持城市的列表
	private ArrayList<City> tour = new ArrayList<City>();
	// 缓存距离
	private double distance = 0;

	// 生成一个空的路径
	public Tour(){
		for (int i = 0; i < SimulatedAnnealing.allCitys.size(); i++) {
			tour.add(null);
		}
	}

	// 复杂路径
	public Tour(ArrayList<City> tour){
		this.tour = (ArrayList<City>) tour.clone();
	}

	public ArrayList<City> getTour(){
		return tour;
	}

	// Creates a random individual
	public void generateIndividual() {
		// Loop through all our destination cities and add them to our tour
		for (int cityIndex = 0; cityIndex < SimulatedAnnealing.allCitys.size(); cityIndex++) {
		  setCity(cityIndex, SimulatedAnnealing.allCitys.get(cityIndex));
		}
		// 随机的打乱
		Collections.shuffle(tour);
	}

	// 获取一个城市
	public City getCity(int tourPos1) {
		return (City)tour.get(tourPos1);
	}

	public void setCity(int tourPosition, City city) {
		tour.set(tourPosition, city);
		// 重新计算距离
		distance = 0;
	}

	// 获得当前距离的 总花费
	public double getDistance(){
		if (distance == 0) {
			double tourDistance = 0;
			for (int cityIndex=0; cityIndex < tourSize(); cityIndex++) {
				City fromCity = getCity(cityIndex);
				City destinationCity;
				if(cityIndex+1 < tourSize()){
					destinationCity = getCity(cityIndex+1);
				}
				else{
					destinationCity = getCity(0);
				}
				tourDistance += fromCity.distanceTo(destinationCity);
			}
			distance = tourDistance;
		}
		return distance;
	}

	// 获得当前路径中城市的数量
	public int tourSize() {
		return tour.size();
	}

	
    public ArrayList<Point>  uppoint() {
    	ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < tourSize(); i++) {
			points.add(Point.fromLngLat(getCity(i).y, getCity(i).x));
		}
		return points;
	}


	@Override
	public String toString() {
		
		String geneString = "(";
		for (int i = 0; i < tourSize(); i++) {
			geneString += getCity(i)+"),(";
		}
		geneString=geneString+")";
		return geneString;
	}
}