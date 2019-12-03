package uk.ac.ed.inf.powergrab;
/**
 * An enum class to enumerates all 3 possible different symbols for station.
 * Then we can use == operator to compare which provides compile-time and run-time safety. 
 * lighthouse means station with positive power and coin
 * zero means station with zero power and coin
 * danger means station with negative power and coin
 *
 */
public enum Symbol {
	lighthouse,
	danger,
	zero

}
