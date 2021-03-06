package uk.ac.ed.inf.powergrab;
/**
 * Main class for the simulator.
 */
public class App 
{
	/**
	 * Read and parse all arguments from the input and pass them to new Play class.
	 * @param args - input arguments
	 */
	public static void main(String[] args)
    {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String mode = args[6];
        new Play(day, month, year, latitude, longitude, seed, mode);
//        test(latitude, longitude, seed, mode);
    }
	
	/**
	 * Run all the days in 2 years with the given latitude, longitude, seed and mode. Print the runtime for every day.
	 * @param latitude, longitude, seed and mode from the input
	 */
	public static void test(double latitude, double longitude, int seed, String mode) {
		int[] years = {2019,2020};
        int[] months = {1,2,3,4,5,6,7,8,9,10,11,12};
        int[] days = {31,28,31,30,31,30,31,31,30,31,30,30};
        long time =0;
        for (int i : years) {
        	for (int j : months) {
        		for (int k = 1; k<=days[j-1];k++) {
        			time = System.currentTimeMillis();
        			new Play((k < 10 ? "0" : "") + Integer.toString(k), (j < 10 ? "0" : "") + Integer.toString(j),
        					Integer.toString(i), latitude, longitude, seed, mode);
        			System.out.println(System.currentTimeMillis()-time);
        		}
        	}
        }
	}
}
