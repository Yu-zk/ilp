package uk.ac.ed.inf.powergrab;
/**
 * An enum class to enumerates all 3 possible different symbols for station.
 * Then we can use == operator to compare which provides compile-time and run-time safety. 
 *
 */
public enum Symbol {
	lighthouse, //station with positive power and coin
	danger,     //station with zero power and coin
	zero        //station with negative power and coin

}
