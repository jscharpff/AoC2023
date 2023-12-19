package challenges.day17;

import java.util.List;

import aocutil.io.FileReader;

public class Day17 {

	/**
	 * Day 17 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/17
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day17.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day17.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + transportLava( ex_input, false ) );
		System.out.println( "Answer : " + transportLava( input, false ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + transportLava( ex_input, true ) );
		System.out.println( "Answer : " + transportLava( input, true ) );
	}

	/**
	 * Finds the path with the lowest heat loss through a grid of city blocks 
	 * 
	 * @param input The list of strings that describes the heat loss for every
	 *   block in the city grid
	 * @param ultramode True to use ultra crucibles that are faster, false for
	 *   normal crucibles
	 * @return The heat loss of the optimal path through the city
	 */
	private static long transportLava( final List<String> input, final boolean ultramode ) {
		return new LavaLogistics( input, ultramode ).findLowestHeatLoss( );
	}
}