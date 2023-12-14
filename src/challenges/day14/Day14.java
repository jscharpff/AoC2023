package challenges.day14;

import java.util.List;

import aocutil.io.FileReader;

public class Day14 {

	/**
	 * Day 14 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/14
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day14.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day14.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Tilts the mirror platform northward and computes the total load on the
	 * northern support beams 
	 * 
	 * @param input The layout of rocks on the platform as a list of strings
	 * @return The total load on the northern beams after tilting
	 */
	private static long part1( final List<String> input ) {
		final MirrorPlatform P = new MirrorPlatform( input );
		return P.getTiltedLoad( );
	}

	/**
	 * Performs a total of 100m tilt-rotate cycles and determines the load on the
	 * northern support beams afterwards. 
	 * 
	 * @param input The layout of rocks on the platform as a list of strings
	 * @return The total load on the northern beams after tilting
	 */
	private static long part2( final List<String> input ) {
		final MirrorPlatform P = new MirrorPlatform( input );
		return P.getCycledLoad( 1000000000 );
	}
}