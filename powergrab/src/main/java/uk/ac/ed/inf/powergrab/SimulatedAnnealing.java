package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Point;

public class SimulatedAnnealing {

	public static List<City> allCitys = new ArrayList<City>();

	//计算 接受的概率
	public static double acceptanceProbability(double currentEnergy, double neighbourEnergy, double temperature) {
		// 如果新的解决方案较优，就接受
		if (neighbourEnergy < currentEnergy) {
			return 1.0;
		}
		return Math.exp((currentEnergy - neighbourEnergy) / temperature);
	}
	
	public static ArrayList<Point> run(ArrayList<Station> stations) {
//		init();
		for (Station s : stations) {
			if (! "danger".equals(s.getSymbol())) {
				allCitys.add(new City(s.getLatitude(), s.getLongitude()));
			}
			
//			System.out.print(new City(s.getLatitude(), s.getLongitude()));
		}
		
		Tour best = sa();
		System.out.println("Final solution distance: " + best.getDistance());
//		System.out.println("Tour: " + best);
		return best.uppoint();
	}

//	public static void main(String[] args) {
//		// 创建所有的城市城市列表
//		init();
//		Tour best = sa();
//		System.out.println("Final solution distance: " + best.getDistance());
//		System.out.println("Tour: " + best);
//	}

	//返回近似的 最佳旅行路径
	private static Tour sa() {
		// 初始化温度
		double temp = 400;

		// 冷却概率
		double coolingRate = 0.0025;

		// 初始化的解决方案
		Tour currentSolution = new Tour();
		currentSolution.generateIndividual();

		System.out.println("Initial solution distance: " + currentSolution.getDistance());

		// 设置当前为最优的方案
		Tour best = new Tour(currentSolution.getTour());

		// 循环知道系统冷却
		while (temp > 1) {
			// 生成一个邻居
			Tour newSolution = new Tour(currentSolution.getTour());

			// 获取随机位置
			int tourPos1 = (int) (newSolution.tourSize() * Math.random());
			int tourPos2 = (int) (newSolution.tourSize() * Math.random());

			City citySwap1 = newSolution.getCity(tourPos1);
			City citySwap2 = newSolution.getCity(tourPos2);

			// 交换
			newSolution.setCity(tourPos2, citySwap1);
			newSolution.setCity(tourPos1, citySwap2);

			// 获得新的解决方案的花费
			double currentEnergy = currentSolution.getDistance();
			double neighbourEnergy = newSolution.getDistance();

			// 决定是否接受新的 方案
			if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > Math.random()) {
				currentSolution = new Tour(newSolution.getTour());
			}

			// 记录找到的最优方案
			if (currentSolution.getDistance() < best.getDistance()) {
				best = new Tour(currentSolution.getTour());
			}

			// 冷却
			temp *= 1-coolingRate;
		}
		return best;
	}

	private static void init() {
		City city = new City(60, 200);
		allCitys.add(city);
		City city2 = new City(180, 200);
		allCitys.add(city2);
		City city3 = new City(80, 180);
		allCitys.add(city3);
		City city4 = new City(140, 180);
		allCitys.add(city4);
		City city5 = new City(20, 160);
		allCitys.add(city5);
		City city6 = new City(100, 160);
		allCitys.add(city6);
		City city7 = new City(200, 160);
		allCitys.add(city7);
		City city8 = new City(140, 140);
		allCitys.add(city8);
		City city9 = new City(40, 120);
		allCitys.add(city9);
		City city10 = new City(100, 120);
		allCitys.add(city10);
		City city11 = new City(180, 100);
		allCitys.add(city11);
		City city12 = new City(60, 80);
		allCitys.add(city12);
		City city13 = new City(120, 80);
		allCitys.add(city13);
		City city14 = new City(180, 60);
		allCitys.add(city14);
		City city15 = new City(20, 40);
		allCitys.add(city15);
		City city16 = new City(100, 40);
		allCitys.add(city16);
		City city17 = new City(200, 40);
		allCitys.add(city17);
		City city18 = new City(20, 20);
		allCitys.add(city18);
		City city19 = new City(60, 20);
		allCitys.add(city19);
		City city20 = new City(160, 20);
		allCitys.add(city20);
	}
}