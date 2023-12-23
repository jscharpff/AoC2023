package challenges.day23;

import java.util.List;

import aocutil.io.FileReader;

public class Day23 {

	/**
	 * Day 23 of the Advent of Code 2023
	 * 
	 * https://adventofcode.com/2023/day/23
	 * 
	 * @param args The command line arguments
	 * @throws Exception
	 */
	public static void main( final String[] args ) throws Exception {
		final List<String> ex_input = new FileReader( Day23.class.getResource( "example.txt" ) ).readLines( );
		final List<String> input = new FileReader( Day23.class.getResource( "input.txt" ) ).readLines( );
		
		System.out.println( "---[ Part 1 ]---" );
		System.out.println( "Example: " + part1( ex_input ) );
		System.out.println( "Answer : " + part1( input ) );

		System.out.println( "\n---[ Part 2 ]---" );
		System.out.println( "Example: " + part2( ex_input ) );
		System.out.println( "Answer : " + part2( input ) );
	}

	/**
	 * Finds the longest route possible over all hike trails described by the
	 * given map
	 * 
	 * @param input The map as a list of strings that describe the terrain and
	 *   slopes
	 * @return The longest path possible given the layout of the terrain and the
	 *   direction of slopes on it
	 */
	private static long part1( final List<String> input ) {
		return new HikeTrailsSloped( input ).findLongestRoute( );
	}

	/**
	 * Again finds the longest route possible over the map, now disregarding the
	 * slopes on the terrain
	 * 
	 * @param input The map as a list of strings that describe the terrain and
	 *   slopes
	 * @return The longest path possible given the layout of the terrain, now
	 *   ignoring slopes
	 */
	private static long part2( final List<String> input ) {
		return new HikeTrails( input ).findLongestRoute( );
	}
	
	// 6661 too high
}