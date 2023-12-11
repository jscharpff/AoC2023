package challenges.day11;

import java.util.List;

import aocutil.io.FileReader;

public class Day11 {

	/**
	 * Day 11 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/11
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day11.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day11.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + CosmicAPSP( ex_input, 1 ) );
		System.out.println( "Answer : " + CosmicAPSP( input, 1 ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + CosmicAPSP( ex_input, 10 ) );
		System.out.println( "Example: " + CosmicAPSP( ex_input, 100 ) );
		System.out.println( "Answer : " + CosmicAPSP( input, 1000000 ) );
	}

	/**
	 * Computes the sum of the cosmic All Pair Shortest Path distances between
	 * all pairs of galaxies in the observed cosmos. Note that the cosmos seems
	 * to be expanding empty space as well...
	 * 
	 * @param input The grid of galaxies observed through a telescope
	 * @param expfactor The expansion factor of empty space in the cosmos
	 * @return The sum of APSP lengths between all galaxies of the cosmos
	 */
	private static long CosmicAPSP( final List<String> input, final int expfactor ) {
		final Cosmos C = new Cosmos( input, expfactor );
		return C.sumShortestDistances( );
	}
}