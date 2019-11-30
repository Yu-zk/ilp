package uk.ac.ed.inf.powergrab;

public class App 
{
	public static void main( String[] args )
    {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String mode = args[6];
        new Play(day, month, year, latitude, longitude, seed, mode);
    }
}
