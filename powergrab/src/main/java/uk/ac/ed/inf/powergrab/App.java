package uk.ac.ed.inf.powergrab;

/**
 * Hello world!
 *
 */
public class App 
{

	
	public static void main( String[] args )
//  input:  15 09 2019 55.944425 -3.188396 5678 stateless
    {
        System.out.println( "Hello World!" );

        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        int seed = Integer.parseInt(args[5]);
        String mode = args[6];
        

        Play p = new Play(day, month, year, latitude, longitude, seed, mode);
        
        


    }
	
	

    
    
}
