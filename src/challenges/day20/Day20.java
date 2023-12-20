package challenges.day20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aocutil.io.FileReader;

public class Day20 {

	/**
	 * Day 20 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/20
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day20.class.getResource( "example.txt" ) ).readLines( );
		final List<String> ex2_input = new FileReader( Day20.class.getResource( "example2.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day20.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Example: " + part1( ex2_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Runs the machine described by the input for 1000 times and returns the
	 * product of high and low signal occurrences
	 * 
	 * @param input The machine gates as a list of strings
	 * @return The product of high and low signal counts over all 1000 machine
	 *   executions
	 */
	private static long part1( final List<String> input ) {
		return new SandMachine( input ).run( 1000 );
	}

	/**
	 * Finds the number of times the start button has to be pressed for the
	 * machine to produce a low signal at output gate rx 
	 * 
	 * @param input The machine gates as a list of strings
	 * @return The number of button presses
	 */
	private static long part2( final List<String> input ) {		
		// some reverse engineering of the input show that rx is fed a low signal
		// exactly when all of the 'terminal' conjunction gates zp, pp, sj, rg
		// (i.e. without child conjunction gates) emit a low signal. Furthermore,
		// these gates can also be divided into four completely independent sub
		// machines as they have no interactions on the input side. Hence we divide
		// the machine into smaller sub machines, one for each terminal, and find
		// the intervals at which they emit a low signal to produce their first
		// common interval		
		final Map<String, Boolean> config = new HashMap<>( );
		config.put( "zp", false );
		config.put( "pp", false );
		config.put( "sj", false );
		config.put( "rg", false );
		return new SandMachine( input ).firstSignalConfiguration( config );
	}
}